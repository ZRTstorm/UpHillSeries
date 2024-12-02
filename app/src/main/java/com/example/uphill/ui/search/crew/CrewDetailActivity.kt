package com.example.uphill.ui.search.crew

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.uphill.MainActivity
import com.example.uphill.R
import com.example.uphill.databinding.ActivityCrewDetailBinding
import com.example.uphill.ui.search.CrewSingleton
import com.google.android.material.tabs.TabLayout

class CrewDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrewDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCrewDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pagerAdapter = FragmentAdapter(supportFragmentManager)
        val pager = findViewById<ViewPager>(R.id.viewPager)
        pager.adapter = pagerAdapter
        val tab = findViewById<TabLayout>(R.id.tab)
        tab.setupWithViewPager(pager)


        // Handle back press using OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Navigate back to MainActivity and select SearchFragment
                CrewSingleton.selectedCrew = null
                val intent = Intent(this@CrewDetailActivity, MainActivity::class.java).apply {
                    putExtra(
                        "navigate_to",
                        R.id.navigation_search
                    ) // Optional, pass navigation target
                }
                startActivity(intent)
                finish() // Close CrewDetailActivity
            }
        })
    }
}
