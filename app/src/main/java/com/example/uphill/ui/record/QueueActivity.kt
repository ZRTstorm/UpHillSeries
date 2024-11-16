package com.example.uphill.ui.record

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uphill.R

class QueueActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_queue)

        // Intent에서 route_id 받기
        val routeId = intent.getIntExtra("routeId", 0)

        if (routeId != 0) { // 0이면 잘못 받음
            // TextView에 route_id 표시
            val routeIdTextView: TextView = findViewById(R.id.textView2)
            routeIdTextView.text = "$routeId" + "번 루트"
            //TODO 서버와 연결해서 텍스트와 이미지 표시
        } else {
            Toast.makeText(this, "Route ID를 받지 못했습니다.", Toast.LENGTH_SHORT).show()
        }

        // 거절 버튼 클릭 리스너 설정
        val reject_button = findViewById<Button>(R.id.button10)
        reject_button.setOnClickListener {
            navigateToRecordFragment()
        }
        // 수락 버튼 클릭 리스너 설정
        val accept_button = findViewById<Button>(R.id.button9)
        accept_button.setOnClickListener {
            routeRegistration(routeId)
        }
    }

    private fun navigateToRecordFragment() {
        // RecordFragment로 이동
        val recordFragment = RecordFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.navigation_record, recordFragment) // fragmentContainer는 FrameLayout ID
            .addToBackStack(null) // 뒤로가기 버튼을 누르면 이전 상태로 돌아가도록 설정
            .commit()
    }
    private fun routeRegistration(routeId : Int){
        // TODO 서버와 연결
        val intent = Intent(this,AcceptActivity::class.java)
        startActivity(intent)
    }
}