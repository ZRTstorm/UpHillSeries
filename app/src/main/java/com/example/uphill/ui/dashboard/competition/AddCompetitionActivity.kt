package com.example.uphill.ui.dashboard.competition

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.httptest2.HttpClient
import com.example.uphill.R
import com.example.uphill.data.model.BattleRoomRegistrySendData
import com.example.uphill.ui.dashboard.DashboardFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AddCompetitionActivity : AppCompatActivity() {

    private var httpJob: Job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + httpJob)
    private val titleBasicString = "제목"
    private val contentBasicString = "내용"
    private var routeNumBasicString = "루트번호"
    @SuppressLint("UseSwitchCompatOrMaterialCode", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_competition)

        val submitButton = findViewById<Button>(R.id.button6)
        val titleEditText = findViewById<EditText>(R.id.editTextText)
        val contentEditText = findViewById<EditText>(R.id.editTextText2)
        val crewOnlySwitch = findViewById<Switch>(R.id.switch2)
        val routeNumEditText = findViewById<EditText>(R.id.editTextText5)


// TextWatcher 설정
//        titleEditText.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//
//            override fun afterTextChanged(s: Editable?) {
//                if (s.toString().isEmpty()) {
//                    titleEditText.setText(titleBasicString)
//                    titleEditText.setSelection(0)  // 커서를 텍스트 시작 위치로 이동
//                }
//            }
//        })
//        contentEditText.addTextChangedListener(object : TextWatcher{
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//
//            override fun afterTextChanged(s: Editable?) {
//                if (s.toString().isEmpty()) {
//                    contentEditText.setText(contentBasicString)
//                    contentEditText.setSelection(0)  // 커서를 텍스트 시작 위치로 이동
//                }
//            }
//        })
//        routeNumEditText.addTextChangedListener(object : TextWatcher{
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//
//            override fun afterTextChanged(s: Editable?) {
//                if (s.toString().isEmpty()) {
//                    routeNumEditText.setText(routeNumBasicString)
//                    routeNumEditText.setSelection(0)  // 커서를 텍스트 시작 위치로 이동
//                }
//            }
//        })
//
//// 포커스 변화 리스너 설정
//        titleEditText.setOnFocusChangeListener { _, hasFocus ->
//            if (hasFocus) {
//                if (titleEditText.text.toString() == titleBasicString) {
//                    titleEditText.setText("")
//                }
//            } else {
//                if (titleEditText.text.toString().isEmpty()) {
//                    titleEditText.setText(titleBasicString)
//                }
//            }
//        }
//        contentEditText.setOnFocusChangeListener { _, hasFocus ->
//            if (hasFocus) {
//                if (contentEditText.text.toString() == contentBasicString) {
//                    contentEditText.setText("")
//                }
//            } else {
//                if (contentEditText.text.toString().isEmpty()) {
//                    contentEditText.setText(contentBasicString)
//                }
//            }
//        }
//        routeNumEditText.setOnFocusChangeListener { _, hasFocus ->
//            if (hasFocus) {
//                if (routeNumEditText.text.toString() == routeNumBasicString) {
//                    routeNumEditText.setText("")
//                }
//            } else {
//                if (routeNumEditText.text.toString().isEmpty()) {
//                    routeNumEditText.setText(routeNumBasicString)
//                }
//            }
//        }





        submitButton.setOnClickListener {
            // Safely convert routeNum to an integer, handling invalid input
            val routeNum = routeNumEditText.text.toString().toIntOrNull()
            if (routeNum == null) {
                routeNumEditText.error = "Please enter a valid route number"
                return@setOnClickListener
            }

            val title = titleEditText.text.toString()
            val content = contentEditText.text.toString()
            val isCrewOnly = crewOnlySwitch.isChecked

            submit(title, content, isCrewOnly, routeNum)
        }
    }

    private fun submit(title: String, content: String, isCrewOnly: Boolean, routeNum: Int) {
        // Create a data object for the request
        val battleRoomData = BattleRoomRegistrySendData(
            content = content,
            crewOpen = isCrewOnly,
            routeId = routeNum,
            title = title
        )

        // Debugging log to verify the data
        println(battleRoomData)

        // TODO: Send the data to the server using HttpClient
        scope.launch {
            HttpClient().registryBattleRoom(battleRoomData) { response ->
                if (response != null) {
                    // 응답 데이터를 다이얼로그로 표시
                    val handler = android.os.Handler(mainLooper)
                    handler.post {
                        val dialog = BattleRoomResultDialog(response)
                        dialog.show(supportFragmentManager, "BattleRoomResultDialog")
                        supportFragmentManager.setFragmentResultListener("inviteCodeDialog", this@AddCompetitionActivity) { _, _ ->
                            //Navigate to the com.example.uphill.ui.dashboard.DashboardFragment
//                            val dashboardFragment = DashboardFragment()
//                            supportFragmentManager.beginTransaction()
//                                .replace(R.id.navigation_dashboard, dashboardFragment) // Replace with the correct container ID
//                                .addToBackStack(null) // Allow navigating back
//                                .commit()
                            onBackPressedDispatcher.onBackPressed()
                        }
                    }
                } else {
                    // 오류 처리 (예: 토스트로 알림)
                    val handler = android.os.Handler(mainLooper)
                    handler.post {
                        Toast.makeText(this@AddCompetitionActivity, "Failed to register battle room", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }



    }
}
