package com.example.uphill.ui.search.crew

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.uphill.MainActivity
import com.example.uphill.R
import com.example.uphill.databinding.ActivityCrewDetailBinding
import com.example.uphill.ui.search.CrewSingleton
import com.google.android.material.bottomnavigation.BottomNavigationView

class CrewDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrewDetailBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCrewDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navCrew: BottomNavigationView = binding.navCrew

        // Handle back press using OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Navigate back to MainActivity and select SearchFragment
                CrewSingleton.selectedCrew = null
                val intent = Intent(this@CrewDetailActivity, MainActivity::class.java).apply {
                    putExtra("navigate_to", R.id.navigation_search) // Optional, pass navigation target
                }
                startActivity(intent)
                finish() // Close CrewDetailActivity
            }
        })

        navController = findNavController(R.id.nav_host_fragment_activity_crewDetail)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_crewMember,
                R.id.navigation_crewDashboard,
                R.id.navigation_crewCompetition
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navCrew.setupWithNavController(navController)

        navCrew.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_crewMember -> {
                    navController.navigate(R.id.navigation_crewMember, Bundle().apply {})
                    true
                }

                R.id.navigation_crewDashboard -> {
                    navController.navigate(R.id.navigation_crewDashboard, Bundle().apply {})
                    true
                }

                R.id.navigation_crewCompetition -> {
                    navController.navigate(R.id.navigation_crewCompetition, Bundle().apply {})
                    true
                }

                else -> false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
