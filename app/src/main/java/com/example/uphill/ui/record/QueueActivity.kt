package com.example.uphill.ui.record

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.httptest2.HttpClient
import com.example.uphill.MainActivity
import com.example.uphill.R
import com.example.uphill.data.UserInfo
import com.example.uphill.http.UphillNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.internal.wait

class QueueActivity : AppCompatActivity() {

    private var httpJob: Job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + httpJob)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_queue)


        val routeId: Int = UserInfo.capturedRouteId?:1


        // 거절 버튼 클릭 리스너 설정
        val rejectButton = findViewById<Button>(R.id.button9)
        rejectButton.setOnClickListener {
            rejectEntry()
        }
        // 수락 버튼 클릭 리스너 설정
        val acceptButton = findViewById<Button>(R.id.button10)
        acceptButton.setOnClickListener {
            acceptEntry(routeId)
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                rejectEntry()
            }
        })
    }
    private fun rejectEntry(){
//        val httpClient = HttpClient()
//        scope.launch {
//            httpClient.rejectEntry()
//        }
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        finish()
    }
    private fun acceptEntry(routeId: Int){
        Log.d("QueueActivity", "accept_button clicked")
        routeRegistration(routeId)
    }


    private fun routeRegistration(routeId: Int){
        // TODO 서버와 연결
        val httpClient = HttpClient()
        scope.launch {
            httpClient.registerEntry(routeId)
        }
        QueueStatus.isRegistered = true
        QueueStatus.routeId = routeId
        scope.launch {
            QueueStatus.nowPosition = httpClient.getEntryPosition()?.count
            QueueStatus.routeImage = httpClient.getRouteImageData(routeId)
        }
        Toast.makeText(this, "${routeId}번 경로 등록 완료", Toast.LENGTH_SHORT).show()
        UphillNotification.createPersistentNotification(applicationContext)
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}