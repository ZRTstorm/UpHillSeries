package com.example.uphill.ui.search.crew

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.uphill.R
import com.example.uphill.databinding.ActivityCrewDetailBinding

class CrewDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrewDetailBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrewDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent에서 데이터 수신
        val crewName = intent.getStringExtra("crew_name") ?: "No Name"
        val crewAdminId = intent.getStringExtra("crew_admin_id") ?: "Unknown Admin"
        val crewNumber = intent.getIntExtra("crew_number", 0)

        // 데이터를 UI에 표시
        binding.crewName.text = crewName
        binding.crewAdminId.text = crewAdminId
        binding.crewNumber.text = "Crew Members: $crewNumber"

        // Join 버튼 클릭 시
        val joinButton = findViewById<Button>(R.id.button14)
        joinButton.setOnClickListener {
            // TODO: 서버에 크루 조인 로직 구현
            finish() // 현재 액티비티 종료로 SearchFragment로 복귀
        }
    }
}
