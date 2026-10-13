package com.example.halaxy_health

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CalendarAdapter(
    private val days: List<Int>,
    private var selected: Int,
    val onSelect: (Int) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val txt = v.findViewById<TextView>(R.id.txtDay)
        val root = v.findViewById<LinearLayout>(R.id.rootDay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        // inflate day cell
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return VH(view)
    }

    override fun getItemCount() = days.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val day = days[position]
        holder.txt.text = day.toString()

        // check if selected
        if (day == selected) {
            // maroon background
            holder.root.setBackgroundResource(R.drawable.bg_day_selected)
            holder.txt.setTextColor(Color.WHITE)
        } else {
            // white background
            holder.root.setBackgroundResource(R.drawable.bg_day_unselected)
            holder.txt.setTextColor(Color.BLACK)
        }

        holder.root.setOnClickListener {
            // update selection
            selected = day
            notifyDataSetChanged()
            onSelect(day)
        }
    }
}