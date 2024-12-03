package com.example.uphill.ui.dashboard.competition

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.httptest2.HttpClient
import com.example.uphill.MainActivity
import com.example.uphill.R
import com.example.uphill.data.UserInfo
import com.example.uphill.data.model.BattleRoomData
import com.example.uphill.ui.dashboard.competition.BattleSingleton.selectedRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text

class CompetitionActivity : AppCompatActivity() {

    private var httpJob: Job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + httpJob)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_competition)


        val competitionData = selectedRoom

        if (competitionData != null) {
            findViewById<TextView>(R.id.titleTextView).text = competitionData.title
            findViewById<TextView>(R.id.descriptionTextView).text = competitionData.adminName
            scope.launch {
                val bdetail = HttpClient().getBattleRoomDetailInfo(competitionData.battleRoomId)
                if (bdetail != null && UserInfo.userId == bdetail.adminId) {
                    withContext(Dispatchers.Main) {
                        val bnt = findViewById<Button>(R.id.delbutton)
                        bnt.visibility = View.VISIBLE

                        bnt.setOnClickListener {
                            scope.launch {
                                HttpClient().deleteBattleRoom(competitionData.battleRoomId)
                                withContext(Dispatchers.Main) {
                                    val intent = Intent(this@CompetitionActivity, MainActivity::class.java)
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                }
            }

        } else {
            findViewById<TextView>(R.id.titleTextView).text = "Competition not found"
        }
    }
}
