package com.example.uphill.ui.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.uphill.databinding.ItemSearchrecyclerBinding

class SearchAdapter(
    private val originalList: ArrayList<Crew>,
    private val onItemClicked: (Crew) -> Unit // 클릭 콜백 추가
) : RecyclerView.Adapter<SearchAdapter.CustomViewHolder>() {

    private var filteredList: ArrayList<Crew> = ArrayList(originalList)

    // ViewHolder 정의
    inner class CustomViewHolder(private val binding: ItemSearchrecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Crew) {
            binding.crewTitle.text = item.crewName
            binding.crewDescription.text = "크루원: ${item.crewNumber}명"

            // 클릭 이벤트 설정
            binding.root.setOnClickListener {
                onItemClicked(item) // 클릭 시 콜백 호출
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = ItemSearchrecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(filteredList[position])
    }

    override fun getItemCount(): Int = filteredList.size

    @SuppressLint("NotifyDataSetChanged")
    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            ArrayList(originalList)
        } else {
            originalList.filter {
                it.crewName.contains(query, ignoreCase = true)
            } as ArrayList<Crew>
        }
        notifyDataSetChanged()
    }
}


