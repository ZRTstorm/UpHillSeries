package com.example.uphill.ui.search.crew.member
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uphill.R
import com.example.uphill.data.model.CrewMan

class CrewMemberAdapter(private val crewManList: List<CrewMan>) :
    RecyclerView.Adapter<CrewMemberAdapter.CrewMemberViewHolder>() {

    class CrewMemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.crew_profile)
        val nameText: TextView = itemView.findViewById(R.id.crewman)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrewMemberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_crewman, parent, false)
        return CrewMemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: CrewMemberViewHolder, position: Int) {
        val crewMan = crewManList[position]
        holder.nameText.text = crewMan.userName

        // Glide를 사용하여 프로필 이미지 로드
        Glide.with(holder.itemView.context)
            .load(crewMan.userProfile)
            .placeholder(R.drawable.ic_information_black_24dp)
            .into(holder.profileImage)
    }

    override fun getItemCount(): Int = crewManList.size
}
