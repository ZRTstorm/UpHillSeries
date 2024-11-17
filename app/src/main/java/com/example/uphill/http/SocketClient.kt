package com.example.uphill.http

import android.util.Log
import com.example.uphill.data.AppStatus
import com.example.uphill.data.UserInfo
import com.example.uphill.objdetection.targetFPS
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.WebSocketListener

import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.headers.StompSubscribeHeaders
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient

object SocketClient : WebSocketListener() {
    private val TAG = "HttpSocketClient"
    val client = StompClient(OkHttpWebSocketClient())
    val url = "copytixe.iptime.org:8080"
    var session: StompSession? = null

    var recordingStartTime:Long = 0
    private var climbingStartTime:Long = 0
    private var climbingEndTime:Long = 0

    private var sessionListenerJob:Job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + sessionListenerJob)

    fun getClimbingStartFrame():Int{
        val startTime = climbingStartTime - recordingStartTime
        Log.d(TAG, "startTime: $startTime")
        val ret = startTime.toInt()*targetFPS/1000
        return ret
    }
    fun getClimbingEndFrame():Int{
        val endTime = climbingEndTime - recordingStartTime
        Log.d(TAG, "endTime: $endTime")
        val ret = endTime.toInt()*targetFPS/1000
        return ret
    }
    fun getClimbingDuration():Int{
        var duration = climbingEndTime - climbingStartTime
        return 0
    }


    fun connect() {
        val entryUrl = "ws://$url/socket-entry?userId=${UserInfo.userId}"
        //val entryUrl = "ws://$url/socket-entry?userId=1"
        val subscriptionUrl = "/queue/notification/${UserInfo.userId}"
        Log.d(TAG, "try connect")



        scope.launch {
            session = client.connect(entryUrl)
            Log.d(TAG, "connected")
            val subscription = session!!.subscribe(StompSubscribeHeaders(
                destination = subscriptionUrl
            ))
            Log.d(TAG, "Waiting for message")
            subscription.collect{ value ->
                var isText = true
                value.headers.forEach { header ->
                    //Log.d(TAG, "${header.key}: ${header.value}")
                    if(header.key == "content-type" && header.value == "application/json"){
                        isText = false
                    }
                }
                if(isText){
                    listenMessage(value.bodyAsText)
                }else{
                    listenJsonMessage(value.bodyAsText)
                }
            }
        }
    }
    private fun listenMessage(message:String){
        Log.d(TAG, "Received message: $message")
        when(message){
            "allowToUseRoute" -> {
                AppStatus.isStart = true
            }
            "start" -> {
                climbingStartTime = System.currentTimeMillis()
            }
            "end" -> {
                climbingEndTime = System.currentTimeMillis()
            }
        }
    }

    private fun listenJsonMessage(message:String){
        val gson = Gson()
        val receivedMessage = gson.fromJson(message, com.example.uphill.data.model.climbingMessage::class.java)
        Log.d(TAG, "message: ${receivedMessage.message}")
        Log.d(TAG, "id: ${receivedMessage.climbingDataId}")
    }

}