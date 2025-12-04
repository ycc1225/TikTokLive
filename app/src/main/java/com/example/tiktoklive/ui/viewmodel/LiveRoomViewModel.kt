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
}