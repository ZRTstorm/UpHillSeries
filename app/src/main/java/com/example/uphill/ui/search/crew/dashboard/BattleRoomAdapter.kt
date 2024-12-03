package com.example.uphill.ui.search.crew.dashboard

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

class BattleRoomAdapter(
    private var battleRoomList: BattleRoomDataList,
    private val onClick: (BattleRoomData) -> Unit
) : RecyclerView.Adapter<BattleRoomAdapter.BattleRoomViewHolder>() {

    inner class BattleRoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.iv_profile)
        private val titleText: TextView = itemView.findViewById(R.id.tv_title)
        private val descriptionText: TextView = itemView.findViewById(R.id.description)
        private val routeText: TextView = itemView.findViewById(R.id.route)

        @SuppressLint("SetTextI18n")
        fun bind(item: BattleRoomData) {
            titleText.text = item.title
            descriptionText.text = "방장: ${item.adminName}"
            routeText.text = "루트:${item.routeId}번"
            itemView.setOnClickListener {
                onClick(item)
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BattleRoomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recyclerview, parent, false)
        return BattleRoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: BattleRoomViewHolder, position: Int) {
        holder.bind(battleRoomList[position])
    }

    override fun getItemCount(): Int = battleRoomList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newItems: BattleRoomDataList) {
        battleRoomList = newItems
        notifyDataSetChanged()
    }
}
