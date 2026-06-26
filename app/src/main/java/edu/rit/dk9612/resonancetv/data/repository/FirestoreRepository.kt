package edu.rit.dk9612.resonancetv.data.repository

import com.google.firebase.firestore.FieldValue // ADD THIS IMPORT
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.rit.dk9612.resonancetv.VideoItem
import edu.rit.dk9612.resonancetv.data.network.SharedVideo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object FirestoreRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val vaultCollection = firestore.collection("community_vault")

    fun shareVideoToVault(video: VideoItem) {
        val sharedVideo = SharedVideo(
            id = video.id,
            title = video.title,
            subtitle = video.subtitle,
            thumbnailUrl = video.thumbnailUrl,
            likes = 0 // Starts at 0
        )
        vaultCollection.document(video.id).set(sharedVideo)
    }

    // NEW: The function to like a video
    fun likeVideo(videoId: String) {
        vaultCollection.document(videoId).update("likes", FieldValue.increment(1))
    }

    fun getCommunityVideosFlow(): Flow<List<VideoItem>> = callbackFlow {
        val listener = vaultCollection
            .orderBy("sharedAt", Query.Direction.DESCENDING)
            .limit(50) // Increased limit for the dedicated screen
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val videos = snapshot?.toObjects(SharedVideo::class.java)?.map { shared ->
                    VideoItem(
                        id = shared.id,
                        title = shared.title,
                        subtitle = shared.subtitle,
                        duration = "Shared Set", // Add this line
                        thumbnailUrl = shared.thumbnailUrl,
                        description = "",
                        likes = shared.likes
                    )
                } ?: emptyList()
                trySend(videos)
            }

        awaitClose { listener.remove() }
    }
}