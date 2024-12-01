package com.example.uphill.ui.search.crew.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uphill.R
import com.example.uphill.data.model.BattleRoomDataList

class BattleRoomAdapter(
    private val battleRoomList: BattleRoomDataList
) : RecyclerView.Adapter<BattleRoomAdapter.BattleRoomViewHolder>() {

    inner class BattleRoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.iv_profile)
        val titleText: TextView = itemView.findViewById(R.id.tv_title)
        val descriptionText: TextView = itemView.findViewById(R.id.description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BattleRoomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recyclerview, parent, false)
        return BattleRoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: BattleRoomViewHolder, position: Int) {
        val battleRoom = battleRoomList[position]
        holder.titleText.text = battleRoom.title
        holder.descriptionText.text = battleRoom.content
    }

    override fun getItemCount(): Int = battleRoomList.size
}
