package com.example.uphill

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.uphill.databinding.ActivityMainBinding
import org.opencv.android.OpenCVLoader

const val TAG = "UPHILL"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_main)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                showExitDialog()
            }
        })
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_information,R.id.navigation_record,R.id.navigation_search
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    navController.navigate(R.id.navigation_home)
                    true
                }
                R.id.navigation_dashboard -> {
                    navController.navigate(R.id.navigation_dashboard)
                    true
                }
                R.id.navigation_record -> {
                    navController.navigate(R.id.navigation_record)
                    true
                }
                R.id.navigation_search -> {
                    navController.navigate(R.id.navigation_search)
                    true
                }
                R.id.navigation_information -> {
                    navController.navigate(R.id.navigation_information)
                    true
                }
                else -> false
            }
        }

    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun showExitDialog(){
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("앱 종료")
        builder.setMessage("앱을 종료하시겠습니까?")
        builder.setPositiveButton("예"){ dialog, _ ->
            dialog.dismiss()
            finishAffinity()
        }
        builder.setNegativeButton("아니오"){ dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }
}