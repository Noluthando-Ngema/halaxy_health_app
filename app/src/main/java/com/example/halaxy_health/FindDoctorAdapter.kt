package com.example.halaxy_health

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class FindDoctorAdapter(private var list: List<Doctor>, val onBook: (Doctor) -> Unit) : RecyclerView.Adapter<FindDoctorAdapter.VH>() {
    class VH(v: View): RecyclerView.ViewHolder(v){
        val txtName = v.findViewById<TextView>(R.id.txtName)
        val txtSpecialty = v.findViewById<TextView>(R.id.txtSpecialty)
        val txtRating = v.findViewById<TextView>(R.id.txtRating)
        val txtNext = v.findViewById<TextView>(R.id.txtNext)
        val img = v.findViewById<ImageView>(R.id.imgDoctor)
        val btn = v.findViewById<Button>(R.id.btnBookNow)
    }
    // TODO: inflate layout and return VH
    override fun onCreateViewHolder(p: ViewGroup, t: Int) = VH(LayoutInflater.from(p.context).inflate(R.layout.item_find_doctor, p, false))
    // function to update list and notify adapter
    override fun getItemCount() = list.size
    // TODO: bind data to views
    override fun onBindViewHolder(h: VH, pos: Int) {
        val d = list[pos]
        h.img.setImageResource(R.drawable.doctor_placeholder)
        h.txtName.text = "Dr. ${d.name} ${d.surname}"
        h.txtSpecialty.text = d.speciality.uppercase()
        h.txtRating.text = when(pos){0->"★ 4.9";1->"★ 4.8";else->"★ 5.0"}
        h.txtNext.text = when(pos){0->"🕒 Next: Today 2:30 PM";1->"🕒 Next: Wed 9:00 AM";else->"🕒 Next: Today 4:00 PM"}
        h.btn.setOnClickListener { onBook(d) }
    }
    fun updateList(newList: List<Doctor>){ list=newList; notifyDataSetChanged() }
}