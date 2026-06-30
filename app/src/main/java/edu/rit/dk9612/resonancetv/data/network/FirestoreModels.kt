package edu.rit.dk9612.resonancetv.data.network

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date


data class SharedVideo(
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val thumbnailUrl: String = "",
    val likedBy: List<String> = emptyList(),
    @ServerTimestamp val sharedAt: Date? = null
)