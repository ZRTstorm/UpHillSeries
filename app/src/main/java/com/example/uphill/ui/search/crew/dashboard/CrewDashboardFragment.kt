package com.example.uphill.ui.search.crew.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.httptest2.HttpClient
import com.example.uphill.R
import com.example.uphill.data.model.BattleRoomDataList
import com.example.uphill.ui.dashboard.competition.BattleSingleton.selectedRoom
import com.example.uphill.ui.dashboard.competition.CompetitionActivity
import com.example.uphill.ui.search.CrewSingleton.selectedCrew
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CrewDashboardFragment : Fragment() {

    private lateinit var adapter: BattleRoomAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView2)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = BattleRoomAdapter(BattleRoomDataList()) { battleRoom ->
            val intent = Intent(requireContext(), CompetitionActivity::class.java)
            selectedRoom = battleRoom
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        val crewData = selectedCrew

        // ViewModel에서 데이터 가져오기
        if (crewData != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val battleRooms = withContext(Dispatchers.IO) {HttpClient().getBattleRoomFromCrewId(crewData.crewId)}
                withContext(Dispatchers.Main) {
                    battleRooms?.let {
                        adapter.updateList(it)
                    }
                }
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_crew_dashboard, container, false)
    }

    fun newInstant() : CrewDashboardFragment
    {
        val args = Bundle()
        val frag = CrewDashboardFragment()
        frag.arguments = args
        return frag
    }
}
