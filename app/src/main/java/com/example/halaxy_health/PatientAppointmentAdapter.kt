package com.example.halaxy_health

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

// recyclerview adapter for displaying patient appointments
class PatientAppointmentAdapter(
    private val list: List<Appointment>,
    val onCancel: (Int) -> Unit,
    val onView: (Appointment) -> Unit
) : RecyclerView.Adapter<PatientAppointmentAdapter.ViewHolder>() {

    // viewholder holds view references for each appointment row
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtRow: TextView = itemView.findViewById(R.id.txtRow)
        val txtDoctor: TextView = itemView.findViewById(R.id.txtDoctor)
        val txtDept: TextView = itemView.findViewById(R.id.txtDept)
        val txtDateTime: TextView = itemView.findViewById(R.id.txtDateTime)
        val txtStatus: TextView = itemView.findViewById(R.id.txtStatus)
        val btnCancel: Button = itemView.findViewById(R.id.btnCancel)
        val btnView: Button = itemView.findViewById(R.id.btnView)
    }

    // inflates the item view layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_patient_appointment, parent, false)
        return ViewHolder(view)
    }

    // returns total item count in list
    override fun getItemCount(): Int = list.size

    // binds appointment data to views
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.txtRow.text = "#${position + 1}"
        holder.txtDoctor.text = item.doctorName
        holder.txtDept.text = item.department
        holder.txtDateTime.text = item.dateTime

        // sets status text, background drawable, and action button visibilities based on status
        when (item.status) {
            "Pending" -> {
                holder.txtStatus.text = "Pending"
                holder.txtStatus.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_status_pending)
                holder.btnCancel.visibility = View.VISIBLE
                holder.btnView.visibility = View.GONE
            }
            "Completed" -> {
                holder.txtStatus.text = "Completed"
                holder.txtStatus.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_status_completed)
                holder.btnView.visibility = View.VISIBLE
                holder.btnCancel.visibility = View.GONE
            }
            "Cancelled" -> {
                holder.txtStatus.text = "Cancelled"
                holder.txtStatus.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_status_cancelled)
                holder.btnView.visibility = View.VISIBLE
                holder.btnCancel.visibility = View.GONE
            }
            "Approved" -> {
                holder.txtStatus.text = "Approved"
                holder.txtStatus.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_status_completed)
                holder.btnCancel.visibility = View.VISIBLE
                holder.btnView.visibility = View.GONE
            }
        }

        // click listeners for action buttons
        holder.btnCancel.setOnClickListener { onCancel(item.id) }
        holder.btnView.setOnClickListener { onView(item) }
    }
}
