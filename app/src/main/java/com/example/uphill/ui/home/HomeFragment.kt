package com.example.uphill.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.httptest2.ClimbingData
import com.example.httptest2.HttpClient
import com.example.uphill.data.AppStatus
import com.example.uphill.data.UserInfo
import com.example.uphill.data.model.AnimationMovementData
import com.example.uphill.data.model.MovementData
import com.example.uphill.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class HomeFragment : Fragment(), ClimbingDataAdapter.OnItemClickListener, ClimbingDataAdapter.OnItemLongClickListener {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var climbingData:ClimbingData? = null//HttpClient(1).getClimbingData()
    var selectedDayClimbingData: ClimbingData? = null

    var selectedDate = LocalDate.now()

    private var httpJob: Job = Job()
    private val httpScope = CoroutineScope(Dispatchers.IO + httpJob)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val calendarView = binding.calendarView
        calendarView.setOnDateChangeListener{ view, year, month, dayofMonth ->
            selectedDate = LocalDate.of(year, month + 1, dayofMonth)
            if (climbingData!=null) {
                updateData(selectedDate)
            }
        }
        CoroutineScope(Dispatchers.IO).launch{
            val httpClient = HttpClient()

            waitForUserID()
            val data = httpClient.getClimbingData()
            if(data!=null){
                if (data.items.size>0){
                    Log.d("test", data.toString())
                }
                climbingData = data
                val handler = Handler(Looper.getMainLooper())
                handler.post{
                    updateData(selectedDate)
                    Log.d(TAG, "get data success, $selectedDate")
                }
            }

        }.start()

        return root
    }
    private suspend fun waitForUserID():Int{
        return withContext(Dispatchers.Default){
            while(UserInfo.userId==null){
                delay(100)
            }
            UserInfo.userId!!
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun updateData(date:LocalDate){
        AppStatus.initAnimationData()
        if(climbingData==null) return
        if(isAdded){
            val recyclerView: RecyclerView = binding.recyclerView
            recyclerView.layoutManager = LinearLayoutManager(this.context)
            selectedDayClimbingData = climbingData!!.getDateData(date)
            val adapter = ClimbingDataAdapter(selectedDayClimbingData!!, this, this)
            recyclerView.adapter = adapter
        }
    }

    override fun onItemClick(position: Int) {
        Log.d(TAG, "Item clicked at position $position, data: ${selectedDayClimbingData?.items?.get(position)}")
        val httpClient = HttpClient()

        httpScope.launch {
            val climbingId = selectedDayClimbingData?.items?.get(position)?.id
            val data = httpClient.getMovementData(climbingId!!)
            if(data!=null){
                if(AppStatus.animationRouteId!=null){
                    if((AppStatus.animationRouteId!!)!=selectedDayClimbingData?.items?.get(position)?.routeId){
                        Log.d(TAG, "wrong route")
                        return@launch
                    }
                }
                AppStatus.animationData = AnimationMovementData(data)

                AppStatus.animationRouteId = selectedDayClimbingData?.items?.get(position)?.routeId
            } else{
                Log.d(TAG, "data is null")
                AppStatus.animationData = null
                return@launch
            }
            val intent = Intent(requireContext(), CompareActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onItemLongClick(position: Int) {
        Log.d(TAG, "Item long clicked at position $position, data: ${selectedDayClimbingData?.items?.get(position)}")

        val httpClient = HttpClient()

        httpScope.launch {
            val climbingId = selectedDayClimbingData?.items?.get(position)?.id
            val data = httpClient.getMovementData(climbingId!!)
            if (data != null) {
                AppStatus.animationRouteId = selectedDayClimbingData?.items?.get(position)?.routeId
                AppStatus.animationData2 = AnimationMovementData(data)
                Log.d(TAG, "set animationData2: ${AppStatus.animationData2}")
            }else{
                Log.d(TAG, "data is null")
                return@launch
            }
        }

    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}