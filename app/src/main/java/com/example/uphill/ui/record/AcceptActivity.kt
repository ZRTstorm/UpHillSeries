package com.example.uphill.ui.record

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.httptest2.HttpClient
import com.example.uphill.MainActivity
import com.example.uphill.R
import com.example.uphill.data.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AcceptActivity : AppCompatActivity() {
    private var httpJob: Job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + httpJob)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_accept)

        val routeId: Int = UserInfo.capturedRouteId?:1

        val countdownTextView: TextView = findViewById(R.id.countdown_text)
        countdownTextView.text = "10"

        Log.d("AcceptActivity", "onCreate called")

        val timer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countdownTextView.text = (millisUntilFinished / 1000).toString()
            }
            override fun onFinish() {
                if(!isFinishing){
                    rejectEntry()
                }
            }
        }
        timer.start()


        val start_button = findViewById<Button>(R.id.button13)
        start_button.setOnClickListener {
            val intent = Intent(this,ShootActivity::class.java)
            startActivity(intent)
        }
        val reject_button = findViewById<Button>(R.id.button7)
        reject_button.setOnClickListener {
            rejectEntry()
        }
    }

    private fun rejectEntry(){
        val httpClient = HttpClient()
        scope.launch {
            httpClient.rejectEntry()
        }
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        finish()
    }
}