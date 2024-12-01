package com.example.uphill.ui.search.crew.dashboard

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.httptest2.HttpClient
import com.example.uphill.R
import com.example.uphill.ui.search.CrewSingleton.selectedCrew
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CrewDashboardFragment : Fragment() {
    private var httpJob: Job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + httpJob)

    private val viewModel: CrewDashboardViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BattleRoomAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_crew_dashboard, container, false)
        val crewData = selectedCrew



        // RecyclerView 초기화
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // ViewModel에서 데이터 가져오기
        if (crewData != null) {
            scope.launch {
                adapter = HttpClient().getBattleRoomFromCrewId(crewData.crewId)
                    ?.let { BattleRoomAdapter(it) }!!
                val handler = android.os.Handler(requireActivity().mainLooper)
                handler.post {
                    recyclerView.adapter = adapter
                }
            }
        }

        return view
    }
}
