// VideoPlayerActivity.kt
package com.example.halaxy_health

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class VideoPlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        val videoId = intent.getStringExtra("videoId")?: ""
        val title = intent.getStringExtra("title")?: ""

        findViewById<TextView>(R.id.txtPlayerTitle).text = title

        val playerView = findViewById<YouTubePlayerView>(R.id.youtubePlayer)
        lifecycle.addObserver(playerView)

        playerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0f)
            }
        })
        // apply dark/light mode
        ThemeHelper.applyTheme(this)
    }
}