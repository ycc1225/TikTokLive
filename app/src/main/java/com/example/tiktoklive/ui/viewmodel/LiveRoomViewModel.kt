package com.example.tiktoklive.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiktoklive.data.model.Comment
import com.example.tiktoklive.data.model.Host
import com.example.tiktoklive.data.service.LiveApiService
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class LiveRoomViewModel : ViewModel() {

    // 主播信息
    private val _hostInfo = MutableLiveData<Host>()
    val hostInfo: LiveData<Host> get() = _hostInfo

    // 聊天信息
    private val _comments = MutableLiveData<List<Comment>>()
    val comments: LiveData<List<Comment>> get() = _comments
    private val _sendSuccess = MutableLiveData<Boolean>()
    val sendSuccess: LiveData<Boolean> get() = _sendSuccess
    private val _errorMsg = MutableLiveData<String>()
    val errorMsg: LiveData<String> get() = _errorMsg

    // 同接人数
    private val _onlineCount = MutableLiveData<Int>()
    val onlineCount: LiveData<Int> get() = _onlineCount

    private var webSocket: WebSocket? =null
    private val client = OkHttpClient()
    private var currentCount = 0

    fun fetchHostInfo(hostId: String) {
        viewModelScope.launch {
            try {
                val host = LiveApiService.api.getHostInfo(hostId)
                _hostInfo.value = host
            } catch (e: Exception) {
                Log.e("LiveRoomVM", "Error fetching host info", e)
                _errorMsg.value = "加载失败: ${e.message}\n${e.cause}"
            }
        }
    }

    fun fetchComments() {
        viewModelScope.launch {
            try {
                val list = LiveApiService.api.getComments()
                _comments.value = list
            } catch (e: Exception) {
                Log.e("LiveRoomVM", "Get comments failed: ${e.message}")
            }
        }
    }

    fun sendComment(content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            try {
                val newComment = LiveApiService.api.sendComment(content)
                val currentList = _comments.value.orEmpty().toMutableList()
                currentList.add(newComment)
                _comments.value = currentList
                _sendSuccess.value = true
            } catch (e: Exception) {
                _errorMsg.value = "发送失败: ${e.message}"
            }
        }
    }

    fun startWebSocket() {
        val request = okhttp3.Request.Builder().url("wss://echo.websocket.org").build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                // 连接成功，开始模拟心跳
                viewModelScope.launch {
                    // 模拟：每隔2秒发一次消息，制造“有人进场”的效果
                    while (true) {
                        kotlinx.coroutines.delay(2000)
                        webSocket.send("heartbeat")
                    }
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                // 收到消息，人数+1
                currentCount++
                _onlineCount.postValue(currentCount)
            }

            // ... onFailure 等其他回调可按需实现 ...
        })
    }

    override fun onCleared() {
        super.onCleared()
        webSocket?.close(1000,"Closed")
    }
}