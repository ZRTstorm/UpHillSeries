package com.example.uphill.http

import android.util.Log
import com.example.uphill.data.UserInfo
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

object SocketClient : WebSocketListener() {
    private val TAG = "HttpSocketClient"
    lateinit var webSocket: WebSocket
    fun startWebSocket(){
        if(UserInfo.userId == null){
            Log.e(TAG, "userId is null")
            return
        }
        Log.d(TAG, "startWebSocket")
        val client = OkHttpClient()
        Log.d(TAG, "ws://copytixe.iptime.org:8080/socket-entry/info?userId=${UserInfo.userId}")
        val request = Request.Builder()
            .url("ws://copytixe.iptime.org:8080/socket-entry?userId=${UserInfo.userId}")
            .build()
        val listener = SocketListener()
        webSocket = client.newWebSocket(request, listener)

        // don't need to maintain client service in background
        // webSocket is singleton
        client.dispatcher.executorService.shutdown()
    }
}