package com.example.uphill.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uphill.R

class CompetitionAdapter(
    private val items: List<Competition>,
    private val onClick: (Competition) -> Unit
) : RecyclerView.Adapter<CompetitionAdapter.CompetitionViewHolder>() {

    inner class CompetitionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleTextView: TextView = view.findViewById(R.id.tv_title)
        private val descriptionTextView: TextView = view.findViewById(R.id.description)

        fun bind(item: Competition) {
            titleTextView.text = item.title
            descriptionTextView.text = item.description
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
}

