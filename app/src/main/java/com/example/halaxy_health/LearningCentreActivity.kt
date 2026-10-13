package com.example.halaxy_health

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.launch

class LearningCentreActivity : AppCompatActivity() {

    // your youtube api key
    private val API_KEY = "AIzaSyBkB6mgyT7Fn0_Bn4Z0AGWrM7vlfM9M0Jw"

    private lateinit var allArticles: List<LearningArticle>
    private lateinit var articleAdapter: LearningArticleAdapter
    private var selectedCategory = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning_centre)

        // views
        val recCat = findViewById<RecyclerView>(R.id.recCategories)
        val recArticles = findViewById<RecyclerView>(R.id.recArticles)
        val recVideos = findViewById<RecyclerView>(R.id.recVideos)
        val progress = findViewById<ProgressBar>(R.id.progress)
        val featuredTitle = findViewById<TextView>(R.id.txtFeaturedTitle)
        val featuredPlayer = findViewById<YouTubePlayerView>(R.id.youtubeFeatured)
        val rootFeatured = findViewById<View>(R.id.rootFeatured)

        // categories
        val categories = listOf("All", "Mental Health", "Diabetes", "Nutrition", "Physio", "Women's Health", "General Practice")

        // local articles - hybrid part
        allArticles = listOf(
            LearningArticle("Understanding Anxiety in Young Adults", "Learn about common triggers, symptoms and when to seek help.", "Mental Health", "Anxiety is common...\nWhen to seek help: speak to GP or psychologist via Halaxy."),
            LearningArticle("Managing Type 2 Diabetes: Diet Tips", "Simple changes to help control blood sugar.", "Diabetes", "Focus on whole grains, lean proteins, low GI foods...\nBook dietitian on Halaxy."),
            LearningArticle("Pelvic Floor Health After Birth", "Why physio is important postpartum.", "Women's Health", "After childbirth, pelvic floor recovery is key...\nFind women's health physio on Halaxy."),
            LearningArticle("Lower Back Pain: When to See a Physio", "Common causes and home stretches.", "Physio", "Lower back pain affects 80%...\nIf pain >2 weeks, book physio."),
            LearningArticle("Healthy Eating on a Budget", "Nutritionist approved tips.", "Nutrition", "Eating healthy not expensive...\nBuy seasonal, plan meals."),
            LearningArticle("What to Expect at First GP Visit", "How to prepare.", "General Practice", "Bring ID, medication list, health history...\nFind GP near you on Halaxy.")
        )

        // setup category chips
        recCat.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recCat.adapter = LearningCategoryAdapter(categories) { cat ->
            selectedCategory = cat
            filterArticles()
        }

        // setup articles
        recArticles.layoutManager = LinearLayoutManager(this)
        articleAdapter = LearningArticleAdapter(allArticles) { article ->
            // open detail
            val i = Intent(this, LearningDetailActivity::class.java)
            i.putExtra("title", article.title)
            i.putExtra("category", article.category)
            i.putExtra("body", article.body)
            startActivity(i)
        }
        recArticles.adapter = articleAdapter

        // setup videos
        recVideos.layoutManager = LinearLayoutManager(this)
        lifecycle.addObserver(featuredPlayer)

        // search filter for articles
        findViewById<EditText>(R.id.edtSearchLearn).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                filterArticles(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // fetch youtube real api
        lifecycleScope.launch {
            try {
                progress.visibility = View.VISIBLE

                // get channel id
                val channelRes = RetrofitClient.api.getChannelId(apiKey = API_KEY)
                val channelId = channelRes.items.first().id

                // get videos
                val videosRes = RetrofitClient.api.getChannelVideos(channelId = channelId, apiKey = API_KEY)
                val ids = videosRes.items.joinToString(",") { it.id.videoId }

                // get details
                val detailsRes = RetrofitClient.api.getVideoDetails(videoIds = ids, apiKey = API_KEY)

                progress.visibility = View.GONE

                // featured
                val featured = detailsRes.items.firstOrNull()
                if (featured!= null) {
                    featuredTitle.text = featured.snippet.title
                    featuredPlayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            youTubePlayer.cueVideo(featured.id, 0f)
                        }
                    })
                    rootFeatured.setOnClickListener {
                        featuredPlayer.visibility = View.VISIBLE
                    }
                }

                // video list
                recVideos.adapter = VideoAdapter(detailsRes.items) { videoId, title ->
                    val i = Intent(this@LearningCentreActivity, VideoPlayerActivity::class.java)
                    i.putExtra("videoId", videoId)
                    i.putExtra("title", title)
                    startActivity(i)
                }

            } catch (e: Exception) {
                progress.visibility = View.GONE
                Toast.makeText(this@LearningCentreActivity, "youtube error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        findViewById<Button>(R.id.btnOpenChannel).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/@halaxycommunity")))
        }
        // apply dark/light mode
        ThemeHelper.applyTheme(this)
        findViewById<Button>(R.id.btnBack).setOnClickListener {
            startActivity(Intent(this, PatientDashboardActivity::class.java))
        }
    }

    private fun filterArticles(search: String = "") {
        var filtered = allArticles
        if (selectedCategory!= "All") {
            filtered = filtered.filter { it.category == selectedCategory }
        }
        if (search.isNotEmpty()) {
            filtered = filtered.filter {
                it.title.contains(search, true) || it.desc.contains(search, true)
            }
        }
        articleAdapter.updateList(filtered)
    }


}