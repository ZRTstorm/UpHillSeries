package com.example.uphill.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uphill.databinding.FragmentSearchBinding
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.SearchView
import com.example.uphill.ui.search.crew.CrewDetailActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.httptest2.HttpClient
import com.example.uphill.data.model.SimpleCrewInfo
import com.example.uphill.data.model.SimpleCrewInfoItem
import kotlinx.coroutines.Job

class SearchFragment : Fragment() {

    private lateinit var searchAdapter: SearchAdapter
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private var httpJob: Job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + httpJob)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val searchView = binding.searchView
        val button15 = binding.button15

        // 검색
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchCrews(query.toString())
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        // 크루 선택
        searchAdapter = SearchAdapter(SimpleCrewInfo()) { crew ->
            CrewSingleton.selectedCrew = crew

            // CrewDetailActivity로 이동
            val intent = Intent(requireContext(), CrewDetailActivity::class.java)
            startActivity(intent)
        }
        binding.searchRecycle.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
        }

        // 크루 데이터 로드
        loadCrewsFromServer()

        button15.setOnClickListener {
            startActivity(Intent(requireContext(), BrandNewCrewActivity::class.java))
        }

        return root
    }
    private fun searchCrews(query: String) {
        Log.d("SearchFragment", "Search query: $query")
        scope.launch {
            val filteredCrews = HttpClient().searchCrews(query)
            if (filteredCrews != null) {
                updateCrewList(filteredCrews)
            }
        }
    }
    private fun updateCrewList(newCrews: SimpleCrewInfo) {
        scope.launch {
            withContext(Dispatchers.Main) { // 메인 스레드에서 실행
                searchAdapter.updateData(newCrews)
            }
        }
    }

    private fun loadCrewsFromServer() {
        CoroutineScope(Dispatchers.IO).launch {
            val crewList = HttpClient().getAllCrew()
            crewList?.let { crews ->
                // 리사이클러뷰 업데이트
                withContext(Dispatchers.Main) {
                    searchAdapter.updateData(crews)
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
