package com.example.uphill.ui.search.crew.dashboard

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.httptest2.HttpClient
import com.example.uphill.R
import com.example.uphill.data.model.BattleRoomDataList
import com.example.uphill.ui.dashboard.competition.BattleSingleton.selectedRoom
import com.example.uphill.ui.dashboard.competition.CompetitionActivity
import com.example.uphill.ui.search.CrewSingleton.selectedCrew
import kotlinx.coroutines.*

class CrewDashboardFragment : Fragment() {

    private lateinit var adapter: BattleRoomAdapter
    private var httpJob: Job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + httpJob)
    private var alertDialog: AlertDialog? = null

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

        // Fetch data for the RecyclerView
        crewData?.let {
            scope.launch {
                while (isActive) { // Coroutine이 활성화된 동안 반복 실행
                    val battleRooms = withContext(Dispatchers.IO) {
                        HttpClient().getBattleRoomFromCrewId(it.crewId)
                    }
                    withContext(Dispatchers.Main) {
                        battleRooms?.let { rooms ->
                            adapter.updateList(rooms)
                        }
                    }
                    delay(10000) // 10초 대기
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

    fun newInstant(): CrewDashboardFragment {
        val args = Bundle()
        val frag = CrewDashboardFragment()
        frag.arguments = args
        return frag
    }

    @SuppressLint("SetTextI18n")
    private fun showConfirmationDialog(id: Int) {
        if (!isAdded) return // Ensure the Fragment is still attached

        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_battleroom, null)
        val titleView = dialogView.findViewById<TextView>(R.id.textView7)

        scope.launch {
            val data = HttpClient().getBattleRoomDetailInfo(id)
            withContext(Dispatchers.Main) {
                if (isAdded) { // Ensure the Fragment is still attached to the activity
                    if (data != null) {
                        titleView.text = "${data.title}\n해당 대회에 등록하시겠습니까?"
                    }
                }
            }
        }

        alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("대회신청")
            .setView(dialogView)
            .setPositiveButton("수락") { _, _ ->
                scope.launch {
                    try {
                        HttpClient().participantBattleRoom(id)
                        withContext(Dispatchers.Main) {
                            val intent = Intent(requireContext(), CompetitionActivity::class.java)
                            startActivity(intent)
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                "이미 등록된 대회입니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                            delay(3000) // 3-second wait
                            val intent = Intent(requireContext(), CompetitionActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }
            .setNegativeButton("취소", null)
            .create()

        alertDialog?.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        alertDialog?.dismiss() // Dismiss the dialog when the Fragment's view is destroyed
        alertDialog = null
        httpJob.cancel() // Cancel any ongoing coroutine job
    }
}
