package com.example.uphill

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class SplashActivity : AppCompatActivity() {

    // 권한 배열
    private val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 카메라와 외부저장소 권한을 확인 후 메인 액티비티로 넘어가게함
        if (arePermissionsGranted()) {
            navigateToMainActivity()
        } else {
            requestPermissionsLauncher.launch(permissions)
        }
    }

    // 권한 확인 되었는지 체크
    private fun arePermissionsGranted(): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    // 권한 요청
    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // 모든 권한이 승인되어야 메인 액티비티로 이동
        if (permissions.all { it.value }) {
            navigateToMainActivity()
        } else {
            navigateToMainActivity()
        }
    }

    // 메인액티비티로 이동
    private fun navigateToMainActivity() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 500) // 0.5초
    }
}
