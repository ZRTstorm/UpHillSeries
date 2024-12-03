package com.example.uphill.ui.search.crew.dashboard

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CrewDashboardFragment : Fragment() {

    private lateinit var adapter: BattleRoomAdapter
    private var httpJob: Job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + httpJob)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView2)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = BattleRoomAdapter(BattleRoomDataList()) { battleRoom ->
            selectedRoom = battleRoom
            showConfirmationDialog(battleRoom.battleRoomId)
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

    @SuppressLint("SetTextI18n")
    private fun showConfirmationDialog(id: Int) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_battleroom, null)
        val titleView = dialogView.findViewById<TextView>(R.id.textView7)
        scope.launch {
            val data = HttpClient().getBattleRoomDetailInfo(id)
            if (data != null) {
                titleView.text = "${data.title}\n해당 대회에 등록하시겠습니까?"
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("대회신청")
            .setView(dialogView)
            .setPositiveButton("수락") {_,_ ->
                try {
                    scope.launch {
                        HttpClient().participantBattleRoom(id)
                    }
                }
                catch(_: Exception){
                    Toast.makeText(requireContext(), "이미 등록된 대회입니다.", Toast.LENGTH_SHORT).show()
                }
                finally {
                    val intent = Intent(requireContext(), CompetitionActivity::class.java)
                    startActivity(intent)
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }
}
