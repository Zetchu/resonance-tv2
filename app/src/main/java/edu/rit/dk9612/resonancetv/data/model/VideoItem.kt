package edu.rit.dk9612.resonancetv.data.model

data class VideoItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val duration: String = "N/A", // Default value here!
    val thumbnailUrl: String = "",
    val description: String = "",
    val likes: Int = 0,
    val isLikedByMe: Boolean = false,
    val sharedAt: java.util.Date? = null
)

