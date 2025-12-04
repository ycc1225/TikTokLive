package com.example.tiktoklive.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiktoklive.data.model.Comment
import com.example.tiktoklive.data.model.Host
import com.example.tiktoklive.data.repository.LiveRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import javax.inject.Inject

@HiltViewModel
class LiveRoomViewModel @Inject constructor(
    private val repository: LiveRepository
) : ViewModel() {
    private val TAG = "LiveRoomVM"

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
                val host = repository.getHostInfo(hostId)
                _hostInfo.value = host
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching host info", e)
                _errorMsg.value = "加载失败: ${e.message}\n${e.cause}"
            }
        }
    }

    fun fetchComments() {
        viewModelScope.launch {
            try {
                val list = repository.getComments()
                _comments.value = list
            } catch (e: Exception) {
                Log.e(TAG, "Get comments failed: ${e.message}")
            }
        }
    }

    fun sendComment(content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            try {
                val newComment = repository.sendComment(content)
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
        repository.startWebSocket(object : LiveRepository.WebSocketCallback{
            override fun onConnected() {
                Log.d(TAG,"连接成功")
            }

            override fun onMessageReceived(text: String) {
                currentCount++
                _onlineCount.postValue(currentCount)
            }

            override fun onFailure(t: Throwable) {
                Log.e(TAG,"失败",t)
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        repository.closeWebSocket()
    }
}