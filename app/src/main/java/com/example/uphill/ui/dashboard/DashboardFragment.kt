package com.example.uphill.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
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
            Competition(2, "Competition 2", "Description 2")
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
}