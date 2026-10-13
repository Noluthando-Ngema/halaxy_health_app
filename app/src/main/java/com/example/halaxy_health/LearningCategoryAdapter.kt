package com.example.halaxy_health

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LearningCategoryAdapter(
    private val categories: List<String>,
    val onSelect: (String) -> Unit
) : RecyclerView.Adapter<LearningCategoryAdapter.VH>() {

    private var selectedPos = 0

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val txt = v.findViewById<TextView>(R.id.txtCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        // inflate chip
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_learning_category, parent, false)
        return VH(view)
    }

    override fun getItemCount() = categories.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val cat = categories[position]
        holder.txt.text = cat

        if (position == selectedPos) {
            // selected chip
            holder.txt.setBackgroundResource(R.drawable.bg_chip_maroon)
            holder.txt.setTextColor(Color.WHITE)
        } else {
            // unselected chip
            holder.txt.setBackgroundResource(R.drawable.bg_chip_grey)
            holder.txt.setTextColor(Color.BLACK)
        }

        holder.txt.setOnClickListener {
            selectedPos = position
            notifyDataSetChanged()
            onSelect(cat)
        }
    }
}