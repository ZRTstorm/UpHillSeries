package com.example.uphill.ui.information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.httptest2.HttpClient
import com.example.uphill.data.UserInfo
import com.example.uphill.databinding.FragmentHomeBinding
import com.example.uphill.databinding.FragmentInformationBinding
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

        btn.setOnClickListener {
            val httpClient = HttpClient()
            scope.launch {
                httpClient.registerEntry(1)
            }
        }

        return root
    }
    }
