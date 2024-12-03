package com.example.uphill.http

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.uphill.R

object UphillNotification {
    var notificationManager: NotificationManager? = null

    fun createPersistentNotification(context: Context) {
        val channelId = "your_channel_id"
        val channelName = "Your Channel Name"

        val intent = Intent(context, YourReceiver::class.java).apply {
            action = "YOUR_ACTION"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager!!.createNotificationChannel(channel)
        }

        val notification = Notification.Builder(context, channelId)
            .setContentTitle("Persistent Notification")
            .setContentText("This notification cannot be dismissed")
            .setSmallIcon(R.drawable.logo)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)


        Log.d("UphillNotification","notification created")

        notificationManager!!.notify(1, notification.build())
    }
}
class YourReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("UphillNotification","function called")
        if (intent?.action == "YOUR_ACTION") {
            // 여기에 실행할 함수 호출 코드 작성
            yourFunction()
        }
    }

    private fun yourFunction() {
        // 이 함수는 알림 클릭 시 실행됩니다.
        Log.d("UphillNotification","function called")
    }
}
