package com.example.uphill.ui.search.crew

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.httptest2.HttpClient
import com.example.uphill.MainActivity
import com.example.uphill.R
import com.example.uphill.databinding.ActivityCrewDetailBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CrewDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrewDetailBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCrewDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navCrew: BottomNavigationView = binding.navCrew
        val crewName = intent.getStringExtra("crewName")

        // 코루틴을 사용하여 네트워크 요청 실행
        CoroutineScope(Dispatchers.Main).launch {
            val crewDatas = withContext(Dispatchers.IO) {
                crewName?.let { HttpClient().searchCrews(it) }
            }

            val crewData = crewDatas?.find { it.crewName == crewName }

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
                        navController.navigate(R.id.navigation_crewMember, Bundle().apply {
                            crewData?.let { putParcelable("crewData", it) }
                        })
                        true
                    }

                    R.id.navigation_crewDashboard -> {
                        navController.navigate(R.id.navigation_crewDashboard, Bundle().apply {
                            crewData?.let { putParcelable("crewData", it) }
                        })
                        true
                    }

                    R.id.navigation_crewCompetition -> {
                        navController.navigate(R.id.navigation_crewCompetition, Bundle().apply {
                            crewData?.let { putParcelable("crewData", it) }
                        })
                        true
                    }

                    else -> false
                }
            }
        }


        fun onSupportNavigateUp(): Boolean {
            return navController.navigateUp() || super.onSupportNavigateUp()
        }

        @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
        fun onBackPressed() {
            super.onBackPressed()
            // Navigate back to MainActivity and select SearchFragment
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("navigate_to", R.id.navigation_search) // Optional, pass navigation target
            }
            startActivity(intent)
            finish() // Close CrewDetailActivity
        }
    }
}
