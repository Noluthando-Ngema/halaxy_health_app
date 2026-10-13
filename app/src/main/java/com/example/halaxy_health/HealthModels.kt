package com.example.halaxy_health

// article model for local articles
data class LearningArticle(
    val title: String,
    val desc: String,
    val category: String,
    val body: String
)

// youtube models
data class ChannelResponse(val items: List<ChannelItem>)
data class ChannelItem(val id: String)
data class VideoResponse(val items: List<VideoItem>)
data class VideoItem(val id: VideoId, val snippet: Snippet)
data class VideoId(val videoId: String)
data class Snippet(val title: String, val publishedAt: String, val thumbnails: Thumbnails)
data class Thumbnails(val medium: ThumbUrl)
data class ThumbUrl(val url: String)
data class VideoDetailResponse(val items: List<VideoDetail>)
data class VideoDetail(val id: String, val snippet: Snippet, val contentDetails: ContentDetails)
data class ContentDetails(val duration: String)