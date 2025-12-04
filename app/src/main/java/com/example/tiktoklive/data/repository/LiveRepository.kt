package com.example.tiktoklive.data.repository

import android.util.Log
import com.example.tiktoklive.data.model.Comment
import com.example.tiktoklive.data.model.Host
import com.example.tiktoklive.data.service.LiveApiService
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LiveRepository {
    suspend fun getHostInfo(hostId: String): Host {
        return LiveApiService.api.getHostInfo(hostId)
    }

    suspend fun getComments(): List<Comment> {
        return LiveApiService.api.getComments()
    }

    suspend fun sendComment(content: String): Comment {
        return LiveApiService.api.sendComment(content)
    }

    //WebSocket

    private var webSocket: WebSocket? = null
    private val client = OkHttpClient()

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

                // 模拟心跳/流量逻辑 (注意：GlobalScope仅用于演示，实际建议传入 CoroutineScope)
                GlobalScope.launch(Dispatchers.IO) {
                    while (webSocket != null) {
                        delay(2000)
                        ws.send("heartbeat")
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