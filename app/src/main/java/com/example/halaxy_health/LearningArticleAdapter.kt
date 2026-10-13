package com.example.halaxy_health

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LearningArticleAdapter(
    private var list: List<LearningArticle>,
    val onRead: (LearningArticle) -> Unit
) : RecyclerView.Adapter<LearningArticleAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val txtCat = v.findViewById<TextView>(R.id.txtArticleCategory)
        val txtTitle = v.findViewById<TextView>(R.id.txtTitle)
        val txtDesc = v.findViewById<TextView>(R.id.txtDesc)
        val btnRead = v.findViewById<TextView>(R.id.btnReadMore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_learning_article, parent, false)
        return VH(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val article = list[position]
        holder.txtCat.text = article.category.uppercase()
        holder.txtTitle.text = article.title
        holder.txtDesc.text = article.desc

        holder.btnRead.setOnClickListener { onRead(article) }
        holder.itemView.setOnClickListener { onRead(article) }
    }

    fun updateList(newList: List<LearningArticle>) {
        list = newList
        notifyDataSetChanged()
    }
}