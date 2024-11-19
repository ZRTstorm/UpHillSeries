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

object SocketClient : WebSocketListener() {
    private val TAG = "HttpSocketClient"
    val url = "copytixe.iptime.org:8080"

    private var endEventHandleFunction: () -> Unit = {}

    var recordingStartTime:Long = 0
    var climbingStartTime:Long = 0
    var climbingEndTime:Long = 0

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

    fun handleEndEvent(){
        endEventHandleFunction()
        endEventHandleFunction = {}
    }
    fun setEndEventHandleFunction(function: () -> Unit){
        endEventHandleFunction = function
    }
}