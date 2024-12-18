package com.example.uphill.ui.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.uphill.databinding.ItemSearchrecyclerBinding
import androidx.recyclerview.widget.DiffUtil
import com.example.uphill.data.model.SimpleCrewInfo
import com.example.uphill.data.model.SimpleCrewInfoItem


class SearchAdapter(
    private var originalList: SimpleCrewInfo, // Use SimpleCrewInfoItem
    private val onItemClicked: (SimpleCrewInfoItem) -> Unit // Update callback type
) : RecyclerView.Adapter<SearchAdapter.CustomViewHolder>() {

    private var filteredList: SimpleCrewInfo = originalList // Filtered list

    // 리사이클러
    inner class CustomViewHolder(private val binding: ItemSearchrecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: SimpleCrewInfoItem) {
            binding.crewTitle.text = item.crewName
            binding.crewDescription.text = item.content

            binding.root.setOnClickListener {
                onItemClicked(item)
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

    fun filter(query: String) {
        val newFilteredList = if (query.isEmpty()) {
            originalList
        } else {
            originalList
        }
        updateList(newFilteredList)
    }

    private fun updateList(newList: SimpleCrewInfo) {
        val diffCallback = DiffUtilCallback(filteredList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        filteredList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateData(newList: SimpleCrewInfo) {
        originalList = newList
        filteredList = newList
        notifyDataSetChanged()
    }
}

class DiffUtilCallback(
    private val oldList: SimpleCrewInfo,
    private val newList: SimpleCrewInfo
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].crewId == newList[newItemPosition].crewId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}


