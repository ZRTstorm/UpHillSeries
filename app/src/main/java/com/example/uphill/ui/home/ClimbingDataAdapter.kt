package com.example.uphill.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.httptest2.ClimbingData
import com.example.uphill.R
import java.util.Locale

class ClimbingDataAdapter(private val itemList: ClimbingData) : RecyclerView.Adapter<ClimbingDataAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.item_text)
        val statusCircle: ImageView = itemView.findViewById(R.id.status_circle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_climbing_data, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList.items[position]
        var str = "루트 번호: ${item.routeId}" +
                "\n등반 시각: ${String.format(Locale.KOREA, "%02d:%02d",item.getCreatedTime()?.hour,item.getCreatedTime()?.minute)}"
                "\n등반 시간: ${item.getClimbingTimeString()}"
        holder.textView.text = str

        // 성공 여부에 따라 원의 색상 변경
        if (item.success) {
            holder.statusCircle.setImageResource(R.drawable.green_circle)
        } else {
            holder.statusCircle.setImageResource(R.drawable.red_circle)
        }
    }

    override fun getItemCount() = itemList.items.size
}
