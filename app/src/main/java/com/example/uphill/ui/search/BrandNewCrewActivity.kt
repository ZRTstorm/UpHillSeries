package com.example.uphill.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.uphill.R
    import android.app.Activity
    import android.content.Intent
    import android.graphics.Bitmap
    import android.net.Uri
    import android.provider.MediaStore
    import android.widget.Button
    import android.widget.EditText
    import android.widget.ImageView
    import com.example.httptest2.HttpClient
import com.example.uphill.MainActivity
import com.example.uphill.data.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BrandNewCrewActivity : AppCompatActivity() {

        private lateinit var imageView2: ImageView
        private lateinit var button16: Button
        private lateinit var button14: Button
        private lateinit var editTextText3: EditText
        private lateinit var editTextText4: EditText
        private lateinit var editTextTextPassword: EditText


        private var httpJob: Job = Job()
        private val scope = CoroutineScope(Dispatchers.IO + httpJob)

        private val PICK_IMAGE_REQUEST = 1
        private var imageUri: Uri? = null

        @SuppressLint("IntentReset")
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_brand_new_crew)

            // 뷰 초기화
            imageView2 = findViewById(R.id.imageView2)
            button16 = findViewById(R.id.button16)
            button14 = findViewById(R.id.button14)
            editTextText3 = findViewById(R.id.editTextText3)
            editTextText4 = findViewById(R.id.editTextText4)
            editTextTextPassword = findViewById(R.id.editTextTextPassword)

            // 갤러리에서 이미지 가져오기
            button16.setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.type = "image/*"
                startActivityForResult(intent, PICK_IMAGE_REQUEST)
            }

            // 작성 내용 서버 전송
            button14.setOnClickListener {
                val crewName = editTextText3.text.toString()
                val content = editTextText4.text.toString()
                val password = editTextTextPassword.text.toString()

                // HttpClient의 createCrew 호출
                if (crewName.isNotEmpty() && content.isNotEmpty() && password.isNotEmpty()) {
                    scope.launch {
                        HttpClient().createCrew(crewName, content, password)
                        UserInfo.crewInfo = HttpClient().getCrewInfo()
                    }
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // 사용자에게 빈 필드 알림 (필요시 추가 가능)
                    println("모든 필드를 입력해주세요.")
                }
            }
        }

        // 갤러리 선택 이미지 처리
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
                imageUri = data?.data
                try {
                    val bitmap: Bitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                    imageView2.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
