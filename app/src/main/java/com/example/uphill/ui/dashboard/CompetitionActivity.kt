package com.example.uphill.ui.dashboard

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.uphill.R

class CompetitionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_competition)

        val competitionId = intent.getIntExtra("competition_id", -1)

        val competition = getCompetitionById(competitionId)
        if (competition != null) {
            findViewById<TextView>(R.id.titleTextView).text = competition.title
            findViewById<TextView>(R.id.descriptionTextView).text = competition.description
        } else {
            findViewById<TextView>(R.id.titleTextView).text = "Competition not found"
        }
    }

    private fun getCompetitionById(id: Int): Competition? {
        val competitionList = listOf(
            Competition(1, 1, "Competition 1", "Description 1"),
            Competition(2, 1, "Competition 2", "Description 2"),
            Competition(3, 1, "Competition 3", "Description 3"),
            Competition(4, 1, "Competition 4", "Description 4"),
            Competition(5, 1, "Competition 5", "Description 5"),
            Competition(6, 1, "Competition 6", "Description 6"),
            Competition(7, 1, "Competition 7", "Description 7"),
            Competition(8, 1, "Competition 8", "Description 8")
        )
        return competitionList.find { it.id == id }
    }
}
