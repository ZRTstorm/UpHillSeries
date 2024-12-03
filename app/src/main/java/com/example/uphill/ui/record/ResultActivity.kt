package com.example.uphill.ui.record

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.uphill.MainActivity
import com.example.uphill.R

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_result)

        QueueStatus.reset()

        // 거절 버튼 클릭 리스너 설정
        val save_button = findViewById<Button>(R.id.button11)
        save_button.setOnClickListener {
            navigateToRecordFragment()
        }
        // 수락 버튼 클릭 리스너 설정
        val del_button = findViewById<Button>(R.id.button12)
        del_button.setOnClickListener {
            navigateToRecordFragment()
        }
    }
    private fun navigateToRecordFragment() {
        // HomeActivity로 이동
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}