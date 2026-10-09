package com.example.halaxy_health

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TableAppointmentAdapter(
    private val list: List<Appointment>,
    private val onApprove: (Int) -> Unit,
    private val onCancel: (Int) -> Unit,
    private val onComplete: (Int) -> Unit
) : RecyclerView.Adapter<TableAppointmentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRow: TextView = view.findViewById(R.id.txtRow)
        val txtName: TextView = view.findViewById(R.id.txtName)
        val txtDateTime: TextView = view.findViewById(R.id.txtDateTime)
        val txtStatus: TextView = view.findViewById(R.id.txtStatus)
        val btnApprove: Button = view.findViewById(R.id.btnApprove)
        val btnCancel: Button = view.findViewById(R.id.btnCancel)
        val btnComplete: Button = view.findViewById(R.id.btnComplete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment_table, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.txtRow.text = (position + 1).toString()
        holder.txtName.text = item.patientName
        holder.txtDateTime.text = item.dateTime

        when (item.status) {
            "Pending" -> {
                holder.txtStatus.text = "Pending"
                holder.txtStatus.setBackgroundColor(Color.parseColor("#FFB74D"))
            }
            "Approved" -> {
                holder.txtStatus.text = "Approved"
                holder.txtStatus.setBackgroundColor(Color.parseColor("#81C784"))
            }
            "Completed" -> {
                holder.txtStatus.text = "Completed"
                holder.txtStatus.setBackgroundColor(Color.parseColor("#90CAF9"))
            }
            "Cancelled" -> {
                holder.txtStatus.text = "Cancelled"
                holder.txtStatus.setBackgroundColor(Color.parseColor("#EF9A9A"))
            }
            else -> {
                holder.txtStatus.text = item.status
                holder.txtStatus.setBackgroundColor(Color.LTGRAY)
            }
        }

        holder.btnApprove.visibility = if (item.status == "Pending") View.VISIBLE else View.GONE
        holder.btnCancel.visibility = if (item.status == "Pending") View.VISIBLE else View.GONE
        holder.btnComplete.visibility = if (item.status == "Approved") View.VISIBLE else View.GONE

        holder.btnApprove.setOnClickListener { onApprove(item.id) }
        holder.btnCancel.setOnClickListener { onCancel(item.id) }
        holder.btnComplete.setOnClickListener { onComplete(item.id) }
    }
}

typealias AppointmentAdapter = TableAppointmentAdapter
