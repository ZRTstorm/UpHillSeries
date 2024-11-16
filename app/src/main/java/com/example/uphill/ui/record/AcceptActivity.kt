package com.example.uphill.ui.record

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.uphill.R

class AcceptActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_accept)

        val start_button = findViewById<Button>(R.id.button13)
        start_button.setOnClickListener {
            val intent = Intent(this,ShootActivity::class.java)
            startActivity(intent)
        }
    }
}