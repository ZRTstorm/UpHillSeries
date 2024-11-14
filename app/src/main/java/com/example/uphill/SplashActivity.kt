package com.example.uphill

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.uphill.data.UserInfo
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class SplashActivity : AppCompatActivity() {
    private lateinit var googleSignInClinet: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if(allPermissionsGranted()){
            Log.d(TAG, "All permission granted")
        }else{
            ActivityCompat.requestPermissions(
                this,getRequiredPermission(), REQUEST_CODE_PERMISSIONS)
        }

        initLogin()

        val signInIntent = googleSignInClinet.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    private fun allPermissionsGranted() = getRequiredPermission().all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }
    private fun getRequiredPermission(): Array<String> {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO,
            )
        } else {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(grantResults.all{it == PackageManager.PERMISSION_GRANTED}){
                Log.d(TAG, "All permission granted")
            }else{
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Permission denied")

                // request permission again
                ActivityCompat.requestPermissions(
                    this,getRequiredPermission(), REQUEST_CODE_PERMISSIONS)
            }
        }
    }


    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
                Log.d(TAG, "Google sign in success")

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }catch (e: ApiException){
                Log.w(TAG, "Google sign in failed", e)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun initLogin(){
        auth = FirebaseAuth.getInstance()
        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClinet = GoogleSignIn.getClient(this, googleSignInOption)
    }
    private fun firebaseAuthWithGoogle(idToken: String){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    UserInfo.user = auth.currentUser
                    Log.d(TAG, "Firebase Auth Success")
                } else {
                    Log.e(TAG, "Firebase Auth Failed")
                }
            }
    }

    companion object{
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val RC_SIGN_IN = 9001
        private const val TAG = "SplashActivity"
    }
}
