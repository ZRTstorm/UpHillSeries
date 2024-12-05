package com.example.uphill.ui.dashboard.competition

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.httptest2.HttpClient
import com.example.uphill.MainActivity
import com.example.uphill.R
import com.example.uphill.data.AppStatus
import com.example.uphill.data.UserInfo
import com.example.uphill.data.model.AnimationMovementData
import com.example.uphill.data.model.BattleRoomClimbingData
import com.example.uphill.ui.dashboard.competition.BattleSingleton.selectedRoom
import com.example.uphill.ui.home.CompareActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class CompetitionActivity : AppCompatActivity(),CompetitionClimbingDataAdapter.OnItemClickListener, CompetitionClimbingDataAdapter.OnItemLongClickListener {

    private var httpJob: Job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + httpJob)

    private var climbingData: BattleRoomClimbingData? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_competition)
        val competitionData = selectedRoom

        if (competitionData != null) {
            findViewById<TextView>(R.id.titleTextView).text = competitionData.title
            findViewById<TextView>(R.id.descriptionTextView).text = competitionData.content
            findViewById<TextView>(R.id.adminTextView).text = "방장: ${competitionData.adminName}"
            scope.launch {
                val bdetail = HttpClient().getBattleRoomDetailInfo(competitionData.battleRoomId)
                val bnt = findViewById<Button>(R.id.delbutton)
                if (bdetail != null && UserInfo.userId == bdetail.adminId) {
                    withContext(Dispatchers.Main) {
                        bnt.setOnClickListener {
                            scope.launch {
                                HttpClient().deleteBattleRoom(competitionData.battleRoomId)
                                withContext(Dispatchers.Main) {
                                    val intent = Intent(this@CompetitionActivity, MainActivity::class.java)
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                }
                else{
                    bnt.text = "탈퇴"
                    bnt.setOnClickListener {
                        scope.launch {
                            HttpClient().quitBattleRoom(competitionData.battleRoomId)
                            withContext(Dispatchers.Main) {
                                val intent = Intent(this@CompetitionActivity, MainActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    }
                }
                climbingData = HttpClient().getBattleRoomClimbingData(competitionData.battleRoomId)
                if(climbingData!=null){
                    updateData()
                }
            }

        } else {
            findViewById<TextView>(R.id.titleTextView).text = "Competition not found"
        }
    }
    private fun updateData(){
        AppStatus.initAnimationData()
        if(climbingData==null) return
        climbingData = climbingData!!.sort()
        val handler = Handler(Looper.getMainLooper())
        handler.post{
            val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.competitionRecyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this)
            val adapter = CompetitionClimbingDataAdapter(climbingData!!, this, this, selectedRoom!!.routeId)
            recyclerView.adapter = adapter
        }
    }
    override fun onItemClick(position: Int) {
        Log.d(TAG, "Item clicked at position $position, data: ${climbingData?.get(position)}")
        val httpClient = HttpClient()

        scope.launch {
            val climbingId = climbingData?.get(position)?.climbingDataId
            val data = httpClient.getMovementData(climbingId!!)
            val climbingProfileUrl = httpClient.getProfileImageUrl(climbingData!![position].userId)
            val bitmap = loadImageFromUri(climbingProfileUrl)
            if(data!=null){
                if(AppStatus.animationRouteId!=null){
//                    if((AppStatus.animationRouteId!!)!=climbingData?.items?.get(position)?.routeId){
//                        Log.d(TAG, "wrong route")
//                        return@launch
//                    }
                }
                AppStatus.animationData = AnimationMovementData(data)

                AppStatus.animationRouteId = selectedRoom!!.routeId
                Log.d(TAG, "animation url: $climbingProfileUrl")
                AppStatus.animationProfile = bitmap
                AppStatus.animationUserName = climbingData!![position].userName
            } else{
                Log.d(TAG, "data is null")
                AppStatus.animationData = null
                return@launch
            }
            val intent = Intent(this@CompetitionActivity, CompareActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onItemLongClick(position: Int) {
        Log.d(TAG, "Item long clicked at position $position, data: ${climbingData?.get(position)}")

        val httpClient = HttpClient()

        scope.launch {
            val climbingId = climbingData?.get(position)?.climbingDataId
            val data = httpClient.getMovementData(climbingId!!)
            val climbingProfileUrl = httpClient.getProfileImageUrl(climbingData!![position].userId)
            val bitmap = loadImageFromUri(climbingProfileUrl)
            UserInfo.photo = bitmap
            Log.d(TAG, "bitmap: $bitmap")
            if (data != null) {
                AppStatus.animationRouteId = selectedRoom!!.routeId
                AppStatus.animationData2 = AnimationMovementData(data)
                AppStatus.animationProfile2 = bitmap
                AppStatus.animationUserName2 = climbingData!![position].userName

                Log.d(TAG, "set animationData2: ${AppStatus.animationData2}")
            }else{
                Log.d(TAG, "data is null")
                return@launch
            }
        }
    }
    private fun loadImageFromUri(uri: String): Bitmap? {
        return try {
            val url = URL(uri)
            val inputStream = url.openStream()
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object{
        private val TAG = CompetitionActivity::class.java.simpleName
    }
}
