package com.example.uphill.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.uphill.databinding.ItemSearchrecyclerBinding

class SearchAdapter(private val list : ArrayList<Post>): RecyclerView.Adapter<SearchAdapter.CustomViewHolder>() {
        inner class CustomViewHolder(private val binding: ItemSearchrecyclerBinding): RecyclerView.ViewHolder(binding.root) {
            fun bind(item: Post) {}
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
            val view = ItemSearchrecyclerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return CustomViewHolder(view)
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            holder.bind(list[position])
        }

        override fun getItemCount()= list.size
    }