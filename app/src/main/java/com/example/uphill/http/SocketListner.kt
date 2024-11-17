package com.example.uphill.http

import android.util.Log
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class SocketListener : WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
        Log.d(TAG, "SocketListener onOpen")
        webSocket.send("Hello, WebSocket!")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
       Log.d(TAG, "SocketListener onMessage $text")
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        Log.d(TAG, "SocketListener onMessage $bytes")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(1000, null)
        Log.d(TAG, "SocketListener onClosing")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
        Log.e(TAG, "SocketListener onFailure", t)
        response?.let {
            Log.e(TAG, "SocketListener onFailure $response")
        }
    }
    companion object{
        const val TAG = "HttpSocketListener"
    }
}
