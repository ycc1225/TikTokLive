package com.example.tiktoklive.data.repository

import com.example.tiktoklive.data.model.Comment
import com.example.tiktoklive.data.model.Host
import com.example.tiktoklive.data.service.LiveApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject

class LiveRepository @Inject constructor(
    private val apiService: LiveApiService
) {
    suspend fun getHostInfo(hostId: String): Host {
        return apiService.getHostInfo(hostId)
    }

    suspend fun getComments(): List<Comment> {
        return apiService.getComments()
    }

    suspend fun sendComment(content: String): Comment {
        return apiService.sendComment(content)
    }

    //WebSocket

    private var webSocket: WebSocket? = null
    private val client = OkHttpClient()

    private var heartbeatJob: Job? = null
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    // 定义一个简单的回调接口，用于将 WebSocket 消息传回 ViewModel
    interface WebSocketCallback {
        fun onMessageReceived(text: String)
        fun onConnected()
        fun onFailure(t: Throwable)
    }

    fun startWebSocket(callback: WebSocketCallback) {
        if (webSocket != null) return // 避免重复连接

        val request = Request.Builder().url("wss://echo.websocket.org").build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
                callback.onConnected()

                heartbeatJob = repositoryScope.launch {
                    while (true) {
                        delay(2000)
                        webSocket?.send("heartbeat") ?: break
                    }
                }
            }

            override fun onMessage(ws: WebSocket, text: String) {
                callback.onMessageReceived(text)
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                callback.onFailure(t)
                webSocket = null
            }

            override fun onClosing(ws: WebSocket, code: Int, reason: String) {
                webSocket = null
            }
        })
    }

    fun closeWebSocket() {
        webSocket?.close(1000, "Close")
        webSocket = null
    }
}