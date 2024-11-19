package com.example.uphill.ui.record

import android.annotation.SuppressLint
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.internal.wait

class QueueActivity : AppCompatActivity() {

    private var httpJob: Job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + httpJob)
    private var isFinished = false
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_queue)


        val routeId: Int = UserInfo.capturedRouteId?:1

        val countdownTextView: TextView = findViewById(R.id.countdown_text)
        countdownTextView.text = "10"

        // 거절 버튼 클릭 리스너 설정
        val rejectButton = findViewById<Button>(R.id.button10)
        rejectButton.setOnClickListener {
            rejectEntry()
        }
        // 수락 버튼 클릭 리스너 설정
        val acceptButton = findViewById<Button>(R.id.button9)
        acceptButton.setOnClickListener {
            acceptEntry()
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                rejectEntry()
            }
        })

        val timer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countdownTextView.text = (millisUntilFinished / 1000).toString()
            }
            override fun onFinish() {
                if(!isFinished){
                    rejectEntry()
                }
            }
        }
        timer.start()



    }
    private fun rejectEntry(){
        val httpClient = HttpClient()
        scope.launch {
            httpClient.rejectEntry()
        }
        isFinished = true
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        finish()
    }
    private fun acceptEntry(){
        Log.d("QueueActivity", "accept_button clicked")
        isFinished = true
        routeRegistration()
    }


    private fun navigateToRecordFragment() {
        // RecordFragment로 이동
        val recordFragment = RecordFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.navigation_record, recordFragment) // fragmentContainer는 FrameLayout ID
            .addToBackStack(null) // 뒤로가기 버튼을 누르면 이전 상태로 돌아가도록 설정
            .commit()
    }
    private fun routeRegistration(){
        // TODO 서버와 연결
        val intent = Intent(this,AcceptActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}