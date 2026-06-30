package edu.rit.dk9612.resonancetv.data.model

data class SearchResponse(
    val items: List<YouTubeItem> = emptyList()
)

data class YouTubeItem(
    val id: VideoId,
    val snippet: Snippet
)

data class VideoId(
    val videoId: String? = null
)

data class Snippet(
    val title: String,
    val description: String,
    val channelTitle: String,
    val thumbnails: Thumbnails
)

data class Thumbnails(
    val high: HighThumbnail
)

data class HighThumbnail(
    val url: String
)