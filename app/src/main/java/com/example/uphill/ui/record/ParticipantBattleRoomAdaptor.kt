package com.example.uphill.ui.record

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uphill.R
import com.example.uphill.data.UserInfo
import com.example.uphill.data.model.BattleRoomData
import com.example.uphill.data.model.BattleRoomDataList

class ParticipantBattleRoomAdaptor (
    private var battleRoomList: BattleRoomDataList, // BattleRoomDataList로 변경
    private val onClick: (BattleRoomData) -> Unit
): RecyclerView.Adapter<ParticipantBattleRoomAdaptor.BattleRoomViewHolder>() {
    private var selectedPosition = RecyclerView.NO_POSITION

    inner class BattleRoomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImage: ImageView = view.findViewById(R.id.br_profile)
        private val titleTextView: TextView = view.findViewById(R.id.br_title)
        private val descriptionTextView: TextView = view.findViewById(R.id.br_description)
        private val routeTextView: TextView = view.findViewById(R.id.br_route)

        @SuppressLint("SetTextI18n")
        fun bind(item: BattleRoomData) {
            titleTextView.text = item.title
            descriptionTextView.text = "방장: ${item.adminName}"
            routeTextView.text = "루트:${item.routeId}번"
            itemView.setOnClickListener {
                onClick(item)
                notifyDataSetChanged()
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BattleRoomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_battleroomview, parent, false)
        return BattleRoomViewHolder(view)
    }
    override fun onBindViewHolder(holder: BattleRoomViewHolder, position: Int) {
        holder.bind(battleRoomList[position])
        if(selectedPosition == position){
            holder.itemView.setBackgroundColor(Color.LTGRAY)
        }else{
            holder.itemView.setBackgroundColor(Color.WHITE)
        }
        holder.itemView.setOnClickListener() {
            selectedPosition = position
            UserInfo.battleRoomId = battleRoomList[position].battleRoomId
            Log.d("ParticipantBattleRoomAdaptor", "battleRoomId: ${UserInfo.battleRoomId}")
            notifyDataSetChanged()
        }
    }
    override fun getItemCount(): Int = battleRoomList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newItems: BattleRoomDataList) {
        battleRoomList = newItems
        notifyDataSetChanged()
    }

}