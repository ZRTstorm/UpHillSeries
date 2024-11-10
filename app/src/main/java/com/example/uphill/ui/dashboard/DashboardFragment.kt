package com.example.uphill.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uphill.R

class DashboardFragment : Fragment() {

    private lateinit var competitionAdapter: CompetitionAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val competitionList = listOf(
            Competition(1, "Competition 1", "Description 1"),
            Competition(2, "Competition 2", "Description 2"),
            Competition(3, "Competition 3", "Description 3"),
            Competition(4, "Competition 4", "Description 4"),
            Competition(5, "Competition 5", "Description 5"),
            Competition(6, "Competition 6", "Description 6"),
            Competition(7, "Competition 7", "Description 7"),
            Competition(8, "Competition 8", "Description 8")
        )

        competitionAdapter = CompetitionAdapter(competitionList) { competition ->
            val intent = Intent(requireContext(), CompetitionActivity::class.java)
            intent.putExtra("competition_id", competition.id)
            startActivity(intent)
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = competitionAdapter
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }
}