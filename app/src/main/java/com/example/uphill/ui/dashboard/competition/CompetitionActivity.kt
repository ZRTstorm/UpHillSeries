package com.example.uphill.ui.dashboard.competition

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
            Competition(1, 1, "TCBC2024", "더클라임에서 연말을 맞이하여 2024-12-20일에 대회를 개최합니다.\n\n" +
                    "남자: 난이도 자율, 난이도 빨강, 난이도 보라, 난이도 회색\n" + "여자: 난이도 자율, 난이도 빨강, 난이도 보라, 난이도 회색 "),
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
