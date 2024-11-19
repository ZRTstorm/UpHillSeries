package com.example.uphill.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uphill.R
import com.example.uphill.ui.dashboard.competition.AddCompetitionActivity
import com.example.uphill.ui.dashboard.competition.Competition
import com.example.uphill.ui.dashboard.competition.CompetitionActivity
import com.example.uphill.ui.dashboard.competition.CompetitionAdapter

class DashboardFragment : Fragment() {

    private lateinit var competitionAdapter: CompetitionAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//       TODO 서버와 연결해서 받아오도록 변경
        val competitionList = listOf(
            Competition(1, 1,"TCBC2024", "더클라임에서 대회개최"),
            Competition(2, 1,"Competition 2", "Description 2"),
            Competition(3, 1,"Competition 3", "Description 3"),
            Competition(4, 1,"Competition 4", "Description 4"),
            Competition(5, 1,"Competition 5", "Description 5"),
            Competition(6, 1,"Competition 6", "Description 6"),
            Competition(7, 1,"Competition 7", "Description 7"),
            Competition(8, 1,"Competition 8", "Description 8")
        )

        competitionAdapter = CompetitionAdapter(competitionList) { competition ->
            val intent = Intent(requireContext(), CompetitionActivity::class.java)
            intent.putExtra("competition_id", competition.id)
            startActivity(intent)
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = competitionAdapter

        val button4 = view.findViewById<Button>(R.id.button4)
        // 클릭 이벤트 설정
        button4.setOnClickListener {
            // AddCompetitionActivity로 이동하는 Intent 생성
            val intent = Intent(requireContext(), AddCompetitionActivity::class.java)
            startActivity(intent)
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }
}