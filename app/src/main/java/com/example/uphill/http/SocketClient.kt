package com.example.uphill.http

import android.util.Log
import com.example.uphill.data.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.headers.StompSubscribeHeaders
import org.hildan.krossbow.stomp.sendText
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient

object SocketClient : WebSocketListener() {
    private val TAG = "HttpSocketClient"
    val client = StompClient(OkHttpWebSocketClient())
    val url = "copytixe.iptime.org:8080"
    var session: StompSession? = null

    private var sessionListenerJob:Job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + sessionListenerJob)


    fun connect() {
        //val entryUrl = "$url/socket-entry?userId=${UserInfo.userId}"
        val entryUrl = "ws://$url/socket-entry?userId=1"
        val subscriptionUrl = "/queue/notification/1"
        Log.d(TAG, "try connect")



        scope.launch {
            session = client.connect(entryUrl)
            Log.d(TAG, "connected")
            val subscription = session!!.subscribe(StompSubscribeHeaders(
                destination = subscriptionUrl
            ))
            Log.d(TAG, "Waiting for message")
            subscription.collect{ value ->
                value.headers.forEach { header ->
                    Log.d(TAG, "${header.key}: ${header.value}")
                }
                listenMessage(value.bodyAsText)
            }
        }
    }
    private fun listenMessage(message:String){
        Log.d(TAG, "Received message: $message")
    }
}