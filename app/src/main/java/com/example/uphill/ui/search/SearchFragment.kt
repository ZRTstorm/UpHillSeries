package com.example.uphill.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uphill.databinding.FragmentSearchBinding
import android.content.Intent
import android.util.Log
import com.example.uphill.ui.search.crew.CrewDetailActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.httptest2.HttpClient

class SearchFragment : Fragment() {

    private lateinit var searchAdapter: SearchAdapter
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize RecyclerView
        searchAdapter = SearchAdapter(arrayListOf()) { crew ->
            CrewSingleton.selectedCrew = crew

            // CrewDetailActivity로 이동
            val intent = Intent(requireContext(), CrewDetailActivity::class.java)
            startActivity(intent)
        }
        binding.searchRecycle.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
        }

        // Load crews from server
        loadCrewsFromServer()

        return root
    }

    private fun loadCrewsFromServer() {
        CoroutineScope(Dispatchers.IO).launch {
            val crewList = HttpClient().getAllCrew() // Fetch the list of crews
            crewList?.let { crews ->
                // Update RecyclerView on the Main Thread
                withContext(Dispatchers.Main) {
                    searchAdapter.updateData(ArrayList(crews))
                }
            } ?: run {
                Log.e("SearchFragment", "Failed to load crews from server")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


