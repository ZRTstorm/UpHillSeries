package com.example.uphill.ui.dashboard

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.uphill.R

// CompetitionActivity.kt
class CompetitionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_competition)

        val competitionId = intent.getIntExtra("competition_id", -1)

        val competition = getCompetitionById(competitionId)
        findViewById<TextView>(R.id.titleTextView).text = competition?.title
        findViewById<TextView>(R.id.descriptionTextView).text = competition?.description
    }

    private fun getCompetitionById(id: Int): Competition? {
        val competitionList = listOf(
            Competition(1, "Competition 1", "Description 1"),
            Competition(2, "Competition 2", "Description 2")
        )
        return competitionList.find { it.id == id }
    }
}
