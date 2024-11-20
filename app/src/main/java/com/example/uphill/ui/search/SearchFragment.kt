package com.example.uphill.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uphill.databinding.FragmentSearchBinding
import android.content.Context
import android.util.Log
import org.json.JSONObject

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null

    private val binding get() = _binding!!
    private lateinit var searchAdapter: SearchAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // RecyclerView 및 어댑터 초기화
        val crewList = loadCrews(requireContext())
        searchAdapter = SearchAdapter(crewList)
        binding.searchRecycle.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
        }

        // SearchView 초기화 및 리스너 설정
        binding.searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchAdapter.filter(it) // 검색어로 필터링
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    searchAdapter.filter(it) // 텍스트 변경 시 필터링
                }
                return true
            }
        })

        return root
    }

    // JSON 파일 읽기
    private fun readJsonFile(context: Context, fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }

    // JSON 파일 목록 가져오기
    private fun getJsonFilesFromAssets(context: Context, folder: String = "crew"): List<String> {
        return context.assets.list(folder)?.filter { it.endsWith(".json") } ?: emptyList()
    }



    // JSON 데이터 파싱
    private fun parseJsonToCrew(jsonString: String): Crew {
        val jsonObject = JSONObject(jsonString)
        val crewId = jsonObject.getInt("crew_id")
        val crewName = jsonObject.getString("crew_name")
        val crewAdminId = jsonObject.getString("crew_adminId")
        val crewMember = jsonObject.getJSONObject("crew_member").toMap()
        val crewNumber = jsonObject.getInt("crew_number")

        return Crew(crewId, crewName, crewAdminId, crewMember.toString(), crewNumber)
    }

    // JSONObject -> Map 확장 함수
    private fun JSONObject.toMap(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        val keys = keys()
        while (keys.hasNext()) {
            val key = keys.next()
            map[key] = getString(key)
        }
        return map
    }

    // Crews 로드
    private fun loadCrews(context: Context): ArrayList<Crew> {
        val list = ArrayList<Crew>()

        // JSON 파일 이름 동적으로 가져오기
        val jsonFiles = getJsonFilesFromAssets(context, "crew")
        jsonFiles.forEach { fileName ->
            val jsonContent = readJsonFile(context, "crew/$fileName")
            val crew = parseJsonToCrew(jsonContent)
            if (crew != null) list.add(crew)
        }


        Log.d("SearchAdapter", "Item count: ${list.size}")
        return list
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
