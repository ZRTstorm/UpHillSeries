package com.example.uphill.http

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.httptest2.HttpClient
import com.example.uphill.R
import com.example.uphill.data.AppStatus
import com.example.uphill.data.UserInfo
import com.example.uphill.ui.record.AcceptActivity
import com.example.uphill.ui.record.QueueActivity
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.headers.StompSubscribeHeaders
import org.hildan.krossbow.stomp.sendText
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient

class WebSocketService: Service() {
    private val TAG = "HttpWebSocketService"
    val client = StompClient(OkHttpWebSocketClient())
    var session: StompSession? = null


    private var sessionListenerJob: Job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + sessionListenerJob)
    private var isServiceStarted = false

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("default", "WebSocket Service", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "WebSocket Service Channel"
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(isServiceStarted){
            return START_STICKY
        }
        isServiceStarted = true
        connect()
        startForeground(1, NotificationCompat.Builder(this, "default").build())
        return START_STICKY
    }

    private fun connect() {


        val entryUrl = "ws://${SocketClient.url}/socket-entry?userId=${UserInfo.userId}"
        val subscriptionUrl = "/queue/notification/${UserInfo.userId}"
        Log.d(TAG, "try connect userId: ${UserInfo.userId}")

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
            session!!.sendText("/app/send", "test")
            session!!.sendText("/app/send", "test")
        }

    }
    private fun listenMessage(message:String){
        Log.d(TAG, "Received message: $message")
        when(message){
            "allowToUseRoute" -> {
                showNotification()
                //TODO: this is debug code. remove under it.
                Log.d(TAG, "allowToUseRoute")
                SocketClient.handleEndEvent()
            }
            "StartToClimbing" -> {
                AppStatus.isStart = true
                SocketClient.climbingStartTime = System.currentTimeMillis()
            }
        }
    }

    private fun listenJsonMessage(message:String){
        val gson = Gson()
        val receivedMessage = gson.fromJson(message, com.example.uphill.data.model.climbingMessage::class.java)
        Log.d(TAG, "message: ${receivedMessage.message}")
        Log.d(TAG, "id: ${receivedMessage.climbingDataId}")

        when(receivedMessage.message){
            "successToClimbing" -> {
                AppStatus.isEnd = true
                UserInfo.lastClimbingId = receivedMessage.climbingDataId
                SocketClient.climbingEndTime = System.currentTimeMillis()
                SocketClient.handleEndEvent()
            }
        }
    }

    private fun showNotification(){
        val intent = Intent(applicationContext, AcceptActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Show alert here
        val builder = NotificationCompat.Builder(this, "default")
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("message")
            .setContentText("test")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager.notify(1, builder.build())
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    override fun onDestroy() {
        super.onDestroy()
    }
}