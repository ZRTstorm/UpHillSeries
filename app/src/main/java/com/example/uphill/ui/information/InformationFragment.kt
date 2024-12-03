package com.example.uphill.ui.information

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.httptest2.HttpClient
import com.example.uphill.data.UserInfo
import com.example.uphill.databinding.FragmentHomeBinding
import com.example.uphill.databinding.FragmentInformationBinding
import com.example.uphill.http.UphillNotification
import com.example.uphill.ui.record.QueueStatus
import com.example.uphill.ui.record.ShootActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class InformationFragment : Fragment() {

    private var _binding: FragmentInformationBinding? = null

    private var httpJob: Job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + httpJob)

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentInformationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if(UserInfo.photo!=null){
            binding.imageView13.setImageBitmap(UserInfo.photo)
        }
        val btn = binding.button
        val btn2 = binding.button2
        val btn3 = binding.button3

        btn.setOnClickListener {
            val httpClient = HttpClient()
            scope.launch {
                httpClient.registerEntry(1)
            }
            QueueStatus.isRegistered = true
            QueueStatus.routeId = 1
            scope.launch {
                QueueStatus.nowPosition = httpClient.getEntryPosition()?.count
                QueueStatus.routeImage = httpClient.getRouteImageData(1)
            }
        }
        btn2.setOnClickListener {
            val intent = Intent(requireContext(), DTestActivity::class.java)
            startActivity(intent)

        }
        btn3.setOnClickListener {
            val intent = Intent(requireContext(), ShootActivity::class.java)
            startActivity(intent)
        }

        return root
    }
    }
