package com.example.uphill.ui.search.crew;

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.uphill.databinding.ActivityCrewDetailBinding

class CrewDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrewDetailBinding

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
    }
}
