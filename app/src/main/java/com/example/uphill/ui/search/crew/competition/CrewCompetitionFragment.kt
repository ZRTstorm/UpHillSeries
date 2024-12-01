package com.example.uphill.ui.search.crew.competition

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.uphill.R
import com.example.uphill.ui.search.CrewSingleton

class CrewCompetitionFragment : Fragment() {

    companion object {
        fun newInstance() = CrewCompetitionFragment()
    }

    private val viewModel: CrewCompetitionViewModel by viewModels()
    val crew = CrewSingleton.selectedCrew

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_crew_competition, container, false)
    }
}