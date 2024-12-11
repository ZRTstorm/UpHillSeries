package com.example.uphill.http

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.subscribeText
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient

class TestClient {
    val client = StompClient(OkHttpWebSocketClient())
    val url = "ws://copytixe.iptime.org:8080/socket-entry?userId=1"


    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private fun connect() {
        runBlocking {
            Log.d(TAG, "Connecting to $url")
            val session = client.connect(url)
            Log.d(TAG, "Connected")
            val subscription = session.subscribeText("/queue/notification/1")


            Log.d(TAG, "Subscribed")
            val collectorJob = scope.launch {
                Log.d(TAG, "Waiting for messages")
                subscription.collect { value ->
                    Log.d(TAG, value)
                }
            }
            delay(300000)
            collectorJob.cancel()
            session.disconnect()
            Log.d(TAG, "Disconnected")
        }
    }
    companion object{
        private const val TAG = "MainActivity"

    }
}