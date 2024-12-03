package com.example.uphill.ui.record

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.httptest2.HttpClient
import com.example.uphill.MainActivity
import com.example.uphill.R
import com.example.uphill.data.UserInfo
import com.example.uphill.data.model.BattleRoomDataList
import com.example.uphill.http.UphillNotification
import com.example.uphill.ui.dashboard.competition.CompetitionAdapter
import com.example.uphill.ui.search.crew.dashboard.BattleRoomAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QueueActivity : AppCompatActivity() {
    private lateinit var battleRoomAdapter: ParticipantBattleRoomAdaptor
    private var httpJob: Job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + httpJob)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_queue)


        val routeId: Int = UserInfo.capturedRouteId?:1
        UserInfo.battleRoomId = null
        Log.d("QueueActivity", "routeId: $routeId")
        
        val textview = findViewById<TextView>(R.id.textView6)
        textview.text = "${routeId}번 루트"

        val recyclerView = findViewById<RecyclerView>(R.id.battleRoomRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        battleRoomAdapter = ParticipantBattleRoomAdaptor(BattleRoomDataList()) { battleRoom ->
            Log.d("QueueActivity", "battleRoom: $battleRoom")
            UserInfo.battleRoomId = battleRoom.battleRoomId
        }
        recyclerView.adapter = battleRoomAdapter
        loadBattleRooms()

        // 거절 버튼 클릭 리스너 설정
        val rejectButton = findViewById<Button>(R.id.button9)
        rejectButton.setOnClickListener {
            rejectEntry()
        }
        // 수락 버튼 클릭 리스너 설정
        val acceptButton = findViewById<Button>(R.id.button10)
        acceptButton.setOnClickListener {
            acceptEntry(routeId)
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                rejectEntry()
            }
        })
    }
    private fun loadBattleRooms() {
        // Coroutine을 사용하여 네트워크 요청을 비동기적으로 실행
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 백그라운드 스레드에서 네트워크 호출
                val battleRooms = withContext(Dispatchers.IO) {
                    HttpClient().getUserBattleRoom()
                }
                val battleRoomDataList = BattleRoomDataList()
                battleRooms?.forEach {
                    if(it.routeId == UserInfo.capturedRouteId)
                    battleRoomDataList.add(it)
                }
                // UI 업데이트 (메인 스레드에서 실행)
                withContext(Dispatchers.Main) {
                    battleRoomAdapter.updateList(battleRoomDataList)
                }
            } catch (e: Exception)  {
                e.printStackTrace() // 네트워크 오류 처리 (로그 출력)
            }
        }
    }
    private fun rejectEntry(){
//        val httpClient = HttpClient()
//        scope.launch {
//            httpClient.rejectEntry()
//        }
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        finish()
    }
    private fun acceptEntry(routeId: Int){
        Log.d("QueueActivity", "accept_button clicked")
        routeRegistration(routeId)
    }


    private fun routeRegistration(routeId: Int){
        val httpClient = HttpClient()
        scope.launch {
            httpClient.registerEntry(routeId)
        }
        QueueStatus.isRegistered = true
        QueueStatus.routeId = routeId
        scope.launch {
            QueueStatus.nowPosition = httpClient.getEntryPosition()?.count
            QueueStatus.routeImage = httpClient.getRouteImageData(routeId)
        }
        Toast.makeText(this, "${routeId}번 경로 등록 완료", Toast.LENGTH_SHORT).show()
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}