package com.example.uphill.ui.home

import android.os.Bundle
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
import com.example.uphill.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var climbingData:ClimbingData? = null//HttpClient(1).getClimbingData()

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
            if (climbingData!=null) {
                val selectedDate = LocalDate.of(year, month + 1, dayofMonth)
                updateData(selectedDate)
            }
        }
        CoroutineScope(Dispatchers.IO).launch{
            val httpClient = HttpClient(1)
            val data = httpClient.getClimbingData()
            if(data!=null){
                if (data.items.size>0){
                    Log.d("test", data.toString())
                }
                climbingData = data
                withContext(Dispatchers.Main){
                    updateData(LocalDate.now())
                }
            }

        }.start()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun updateData(date:LocalDate){
        if(climbingData==null) return
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        val data = climbingData!!.getDateData(date)
        val adapter = ClimbingDataAdapter(data)
        recyclerView.adapter = adapter
    }
}