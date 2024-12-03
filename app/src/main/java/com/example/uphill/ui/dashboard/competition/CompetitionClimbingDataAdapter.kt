package com.example.uphill.ui.dashboard.competition

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.httptest2.ClimbingData
import com.example.httptest2.HttpClient
import com.example.uphill.R
import com.example.uphill.data.model.BattleRoomClimbingData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Locale

class CompetitionClimbingDataAdapter(private val itemList: BattleRoomClimbingData, private val clickListener: OnItemClickListener, private val longClickListener: OnItemLongClickListener, private val routeId: Int) : RecyclerView.Adapter<CompetitionClimbingDataAdapter.ViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION
    private var httpJob: Job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + httpJob)
    private var updateChangeCount = 0

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.item_text)
        val statusCircle: ImageView = itemView.findViewById(R.id.status_circle)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    clickListener.onItemClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_climbing_data, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        updateChangeCount = 0
        val item = itemList[position]
        val str = "${position+1}위" +
                "\n등반 선수: ${item.userName}" +
                "\n등반 시간: ${item.getClimbingTimeString()}"
        holder.textView.text = str

        if(selectedPosition == position){
            holder.itemView.setBackgroundColor(Color.LTGRAY)
            holder.textView.setTextColor(Color.BLACK)
        }else{
            holder.itemView.setBackgroundColor(Color.WHITE)
            holder.textView.setTextColor(Color.BLACK)
        }
        holder.itemView.setOnLongClickListener {
            selectedPosition = position
            notifyDataSetChanged()
            longClickListener.onItemLongClick(position)
            true
        }

        // 성공 여부에 따라 원의 색상 변경
//        if (item.success) {
//            holder.statusCircle.setImageResource(R.drawable.green_circle)
//
//        } else {
//            holder.statusCircle.setImageResource(R.drawable.red_circle)
//        }
        updateImage(holder, holder.statusCircle, item.userId)
    }
    private fun updateImage(holder: ViewHolder, view: ImageView, userId:Int){
        scope.launch {

            val imageUrl = HttpClient().getProfileImageUrl(userId)

            val handler = android.os.Handler(android.os.Looper.getMainLooper())
            handler.post {
                val layoutParams = view.layoutParams
                layoutParams.width = 100
                layoutParams.height = 100
                view.layoutParams = layoutParams
                Glide.with(holder.itemView.context)
                    .load(imageUrl)
                    .into(view)
            }
            updateChangeCount++
        }
        if(updateChangeCount==itemList.size){
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = itemList.size

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
    interface OnItemLongClickListener {
        fun onItemLongClick(position: Int)
    }
}
