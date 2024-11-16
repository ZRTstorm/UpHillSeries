package com.example.uphill.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uphill.R
import com.example.uphill.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null

    private val binding get() = _binding!!
    private lateinit var searchAdapter: SearchAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val informationViewModel =
            ViewModelProvider(this).get(SearchViewModel::class.java)

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // TODO 서버에서 받아와 POST리스트에 넣도록 변경
        val list = ArrayList<Post>()
        list.add(Post(R.drawable.logo))
        list.add(Post(R.drawable.logo))
        list.add(Post(R.drawable.logo))
        list.add(Post(R.drawable.logo))
        list.add(Post(R.drawable.logo))
        list.add(Post(R.drawable.logo))
        list.add(Post(R.drawable.logo))
        list.add(Post(R.drawable.logo))
        list.add(Post(R.drawable.logo))
        list.add(Post(R.drawable.logo))

        // 어댑터 초기화
        searchAdapter = SearchAdapter(list)

        // RecyclerView 설정
        binding.searchRecycle.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Post 데이터 클래스 정의
data class Post(val imageResId: Int)

fun searchcrew(){
    //TODO 서버와 연결 필요
}
