package com.example.uphill.ui.dashboard.competition

import android.annotation.SuppressLint
import android.os.Bundle
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
        val routeNumEditText = findViewById<EditText>(R.id.editTextText5)

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
        HttpClient().registryBattleRoom(battleRoomData) { response ->
            if (response != null) {
                // 응답 데이터를 다이얼로그로 표시
                val dialog = BattleRoomResultDialog(response)
                dialog.show(supportFragmentManager, "BattleRoomResultDialog")
            } else {
                // 오류 처리 (예: 토스트로 알림)
                Toast.makeText(this, "Failed to register battle room", Toast.LENGTH_SHORT).show()
            }
        }


        // Navigate to the com.example.uphill.ui.dashboard.DashboardFragment
        val dashboardFragment = DashboardFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.navigation_dashboard, dashboardFragment) // Replace with the correct container ID
            .addToBackStack(null) // Allow navigating back
            .commit()
    }
}
