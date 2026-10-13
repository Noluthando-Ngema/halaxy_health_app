package com.example.halaxy_health

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LearningDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning_detail)

        val title = intent.getStringExtra("title")?: ""
        val category = intent.getStringExtra("category")?: ""
        val body = intent.getStringExtra("body")?: ""

        findViewById<TextView>(R.id.txtDetailCategory).text = category.uppercase()
        findViewById<TextView>(R.id.txtDetailTitle).text = title
        findViewById<TextView>(R.id.txtDetailBody).text = body
    }
}