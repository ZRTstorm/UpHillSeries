package com.example.uphill.ui.dashboard.competition

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.uphill.R
import com.example.uphill.data.model.BattleRoomData
import com.example.uphill.ui.dashboard.competition.BattleSingleton.selectedRoom
import org.w3c.dom.Text

class CompetitionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_competition)

        val competitionData = selectedRoom

        if (competitionData != null) {
            findViewById<TextView>(R.id.titleTextView).text = competitionData.title
            findViewById<TextView>(R.id.descriptionTextView).text = competitionData.adminName
        } else {
            findViewById<TextView>(R.id.titleTextView).text = "Competition not found"
        }
    }
}
