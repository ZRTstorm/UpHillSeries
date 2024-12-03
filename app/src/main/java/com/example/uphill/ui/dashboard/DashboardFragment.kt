package com.example.uphill.ui.dashboard

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.httptest2.HttpClient
import com.example.uphill.R
import com.example.uphill.data.model.BattleRoomDataList
import com.example.uphill.ui.dashboard.competition.AddCompetitionActivity
import com.example.uphill.ui.dashboard.competition.CompetitionActivity
import com.example.uphill.ui.dashboard.competition.CompetitionAdapter
import com.example.uphill.ui.dashboard.competition.BattleSingleton.selectedRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.utf8Size

class DashboardFragment : Fragment() {

    private lateinit var battleRoomAdapter: CompetitionAdapter
    private var httpJob: Job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + httpJob)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 기본적으로 빈 리스트로 초기화
        battleRoomAdapter = CompetitionAdapter(BattleRoomDataList()) { battleRoom ->
            val intent = Intent(requireContext(), CompetitionActivity::class.java)
            selectedRoom = battleRoom
            startActivity(intent)
        }
        recyclerView.adapter = battleRoomAdapter

        // 서버에서 데이터 가져오기 (Coroutine 사용)
        loadBattleRooms()

        val button4 = view.findViewById<Button>(R.id.button4)
        button4.setOnClickListener {
            val intent = Intent(requireContext(), AddCompetitionActivity::class.java)
            startActivity(intent)
        }

        val searchView = view.findViewById<SearchView>(R.id.searchView2)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    if(query.length == 8){
                        showConfirmationDialog(query)
                    }
                }
                searchView.setQuery("", false)
                searchView.clearFocus()
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

    }

    override fun onResume() {
        super.onResume()
        loadBattleRooms()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    private fun loadBattleRooms() {
        // Coroutine을 사용하여 네트워크 요청을 비동기적으로 실행
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 백그라운드 스레드에서 네트워크 호출
                val battleRooms = withContext(Dispatchers.IO) {
                    HttpClient().getUserBattleRoom()
                }
                // UI 업데이트 (메인 스레드에서 실행)
                withContext(Dispatchers.Main) {
                    battleRooms?.let {
                        battleRoomAdapter.updateList(it)
                    }
                }
            } catch (e: Exception)  {
                e.printStackTrace() // 네트워크 오류 처리 (로그 출력)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showConfirmationDialog(code: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_battleroom, null)
        val TitleView = dialogView.findViewById<TextView>(R.id.textView7)
        scope.launch {
            val data = HttpClient().getBattleRoomFromCode(code)
            if (data != null) {
                TitleView.text = "${data.title}\n해당 대회에 등록하시겠습니까?"
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("대회신청")
            .setView(dialogView)
            .setPositiveButton("수락") {_,_ ->
                    scope.launch {
                        HttpClient().participantBattleRoom(code)
                    }
                }
                .setNegativeButton("취소", null)
                .show()
        loadBattleRooms()
    }
}


