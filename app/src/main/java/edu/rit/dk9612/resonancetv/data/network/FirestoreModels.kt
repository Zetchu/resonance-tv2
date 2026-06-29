package edu.rit.dk9612.resonancetv.data.network

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

// This is the data structure that gets sent to and fetched from Firebase
data class SharedVideo(
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val thumbnailUrl: String = "",
    val likedBy: List<String> = emptyList(),
    @ServerTimestamp val sharedAt: Date? = null // Firebase will automatically fill this in!
)