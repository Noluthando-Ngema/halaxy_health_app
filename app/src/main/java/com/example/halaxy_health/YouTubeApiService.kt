package com.example.halaxy_health

import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApiService {

    @GET("channels")
    suspend fun getChannelId(
        @Query("part") part: String = "id",
        @Query("forHandle") handle: String = "halaxycommunity",
        @Query("key") apiKey: String
    ): ChannelResponse

    @GET("search")
    suspend fun getChannelVideos(
        @Query("part") part: String = "snippet",
        @Query("channelId") channelId: String,
        @Query("order") order: String = "date",
        @Query("type") type: String = "video",
        @Query("maxResults") max: Int = 10,
        @Query("key") apiKey: String
    ): VideoResponse

    @GET("videos")
    suspend fun getVideoDetails(
        @Query("part") part: String = "contentDetails,snippet",
        @Query("id") videoIds: String,
        @Query("key") apiKey: String
    ): VideoDetailResponse
}