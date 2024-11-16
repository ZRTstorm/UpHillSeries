package com.example.uphill.ui.dashboard.competition

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.uphill.R
import com.example.uphill.ui.dashboard.DashboardFragment


class AddCompetitionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_competition)
        val del_button = findViewById<Button>(R.id.button6)
        del_button.setOnClickListener {
            submit()
        }
    }
    private fun submit(){
        //TODO 서버에 전송
        val dashboardFragment = DashboardFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.navigation_dashboard, dashboardFragment) // fragmentContainer는 FrameLayout ID
            .addToBackStack(null) // 뒤로가기 버튼을 누르면 이전 상태로 돌아가도록 설정
            .commit()
    }
}

