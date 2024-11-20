package com.example.uphill.ui.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.uphill.databinding.ItemSearchrecyclerBinding

class SearchAdapter(private val originalList: ArrayList<Crew>) :
    RecyclerView.Adapter<SearchAdapter.CustomViewHolder>() {

    private var filteredList: ArrayList<Crew> = ArrayList(originalList)

    // ViewHolder 정의
    inner class CustomViewHolder(private val binding: ItemSearchrecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Crew) {
            binding.crewTitle.text = item.crewName
            binding.crewDescription.text = "크루원: ${item.crewNumber}명"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = ItemSearchrecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(filteredList[position])
    }

    override fun getItemCount() = filteredList.size

    // 필터링 로직 추가
    @SuppressLint("NotifyDataSetChanged")
    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            ArrayList(originalList) // 검색어가 비어 있으면 전체 데이터
        } else {
            originalList.filter {
                it.crewName.contains(query, ignoreCase = true) // crewTitle 기준 필터링
            } as ArrayList<Crew>
        }
        notifyDataSetChanged() // RecyclerView 갱신
    }
}

