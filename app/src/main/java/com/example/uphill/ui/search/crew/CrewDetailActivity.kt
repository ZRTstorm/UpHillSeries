package com.example.uphill.ui.search.crew;

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.uphill.R
import com.example.uphill.databinding.ActivityCrewDetailBinding
import com.example.uphill.ui.search.SearchFragment

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

        val join_button = findViewById<Button>(R.id.button14)
        join_button.setOnClickListener {
            //TODO 서버에 크루조인

            val searchFragment = SearchFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.navigation_search, searchFragment) // fragmentContainer는 FrameLayout ID
                .commit()
        }
    }
}
