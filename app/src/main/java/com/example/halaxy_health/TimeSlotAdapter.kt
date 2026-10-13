package com.example.halaxy_health

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class TimeSlotAdapter(
    private val slots: List<String>,
    private val booked: List<String>,
    val onSelect: (String) -> Unit
) : RecyclerView.Adapter<TimeSlotAdapter.VH>() {

    private var selectedPos = -1

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val txt = v.findViewById<TextView>(R.id.txtTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        // inflate time cell
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_time_slot, parent, false)
        return VH(view)
    }

    override fun getItemCount() = slots.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val time = slots[position]
        holder.txt.text = time

        // check if already booked
        if (booked.contains(time)) {
            // grey out booked slot
            holder.txt.setBackgroundResource(R.drawable.bg_time_booked)
            holder.txt.isEnabled = false
            holder.txt.alpha = 0.4f
            holder.txt.setTextColor(Color.GRAY)
        } else {
            // available slot
            holder.txt.isEnabled = true
            holder.txt.alpha = 1f

            if (position == selectedPos) {
                // selected state
                holder.txt.setBackgroundResource(R.drawable.bg_time_selected)
                holder.txt.setTextColor(Color.WHITE)
            } else {
                // unselected state
                holder.txt.setBackgroundResource(R.drawable.bg_time_unselected)
                holder.txt.setTextColor(Color.BLACK)
            }

            holder.txt.setOnClickListener {
                // select this time
                selectedPos = position
                notifyDataSetChanged()
                onSelect(time)
            }
        }
    }
}