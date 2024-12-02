package com.example.uphill.ui.dashboard.competition

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uphill.R
import com.example.uphill.data.model.BattleRoomData
import com.example.uphill.data.model.BattleRoomDataList

class CompetitionAdapter(
    private var items: BattleRoomDataList, // BattleRoomDataList로 변경
    private val onClick: (BattleRoomData) -> Unit
) : RecyclerView.Adapter<CompetitionAdapter.CompetitionViewHolder>() {

    inner class CompetitionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImage: ImageView = itemView.findViewById(R.id.iv_profile)
        private val titleTextView: TextView = view.findViewById(R.id.tv_title)
        private val descriptionTextView: TextView = view.findViewById(R.id.description)
        private val routeTextView: TextView = view.findViewById(R.id.route)

        @SuppressLint("SetTextI18n")
        fun bind(item: BattleRoomData) {
            titleTextView.text = item.title
            descriptionTextView.text = "방장: ${item.adminName}"
            routeTextView.text = "루트:${item.routeId}번"
            itemView.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompetitionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recyclerview, parent, false)
        return CompetitionViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompetitionViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newItems: BattleRoomDataList) {
        items = newItems
        notifyDataSetChanged()
    }
}

