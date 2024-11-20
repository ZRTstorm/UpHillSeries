package com.example.uphill.ui.dashboard.competition

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.uphill.R
import com.example.uphill.ui.dashboard.DashboardFragment
import org.json.JSONObject


class AddCompetitionActivity : AppCompatActivity() {
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_competition)

        val submitButton = findViewById<Button>(R.id.button6)
        val titleEditText = findViewById<EditText>(R.id.editTextText)
        val contentEditText = findViewById<EditText>(R.id.editTextText2)
        val crewOnlySwitch = findViewById<Switch>(R.id.switch2)

        val title = titleEditText.text.toString()
        val content = contentEditText.text.toString()
        val isCrewOnly = crewOnlySwitch.isChecked
//        Json 형식
//        {
//            "title": "대회방이름",
//            "content": "대회방 설명",
//            "isCrewOnly": true,
//        }
        submitButton.setOnClickListener {
            submit(title, content, isCrewOnly)
        }
    }
    private fun submit(title: String, content: String, isCrewOnly: Boolean){

        // JSON 객체 생성
        val jsonObject = JSONObject().apply {
            put("title", title)
            put("content", content)
            put("isCrewOnly", isCrewOnly)
        }

        // JSON 출력
        println(jsonObject.toString())

        //TODO 서버에 전송

        // Dashboard Fragment로 이동
        val dashboardFragment = DashboardFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.navigation_dashboard, dashboardFragment) // fragmentContainer는 FrameLayout ID
            .addToBackStack(null) // 뒤로가기 버튼을 누르면 이전 상태로 돌아가도록 설정
            .commit()
    }
}

