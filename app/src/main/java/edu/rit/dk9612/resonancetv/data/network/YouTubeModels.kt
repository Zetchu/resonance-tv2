package edu.rit.dk9612.resonancetv.data.network

import com.google.gson.annotations.SerializedName

data class YouTubeSearchResponse(
    val items: List<YouTubeItem>
)

data class YouTubeItem(
    val id: VideoId,
    val snippet: VideoSnippet
)

data class VideoId(
    val videoId: String?
)

data class VideoSnippet(
    val title: String,
    val description: String,
    val channelTitle: String,
    val thumbnails: Thumbnails // Add this!
)

// Add these new classes to map the thumbnail URLs
data class Thumbnails(
    val medium: ThumbnailInfo,
    val high: ThumbnailInfo
)

data class ThumbnailInfo(
    val url: String
)