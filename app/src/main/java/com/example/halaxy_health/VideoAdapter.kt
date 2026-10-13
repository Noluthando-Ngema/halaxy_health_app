package com.example.halaxy_health

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class VideoAdapter(
    private val list: List<VideoDetail>,
    val onClick: (String, String) -> Unit
) : RecyclerView.Adapter<VideoAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val title = v.findViewById<TextView>(R.id.txtVideoTitle)
        val date = v.findViewById<TextView>(R.id.txtVideoDate)
        val thumb = v.findViewById<ImageView>(R.id.imgThumb)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_video_row, parent, false)
        return VH(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]
        val duration = parseDuration(item.contentDetails.duration)
        holder.title.text = "▶ ${item.snippet.title} ($duration)"
        holder.date.text = item.snippet.publishedAt.take(10)
        Glide.with(holder.itemView.context)
            .load(item.snippet.thumbnails.medium.url)
            .into(holder.thumb)
        holder.itemView.setOnClickListener { onClick(item.id, item.snippet.title) }
    }

    fun parseDuration(iso: String): String {
        val regex = Regex("PT(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?")
        val m = regex.find(iso)
        val h = m?.groupValues?.get(1)?.toIntOrNull()?:0
        val min = m?.groupValues?.get(2)?.toIntOrNull()?:0
        val sec = m?.groupValues?.get(3)?.toIntOrNull()?:0
        return if (h>0) String.format("%d:%02d:%02d", h,min,sec)
        else String.format("%d:%02d", min,sec)
    }
}