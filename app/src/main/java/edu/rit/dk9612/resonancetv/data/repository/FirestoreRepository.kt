package edu.rit.dk9612.resonancetv.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue // ADD THIS IMPORT
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.rit.dk9612.resonancetv.data.model.VideoItem
import edu.rit.dk9612.resonancetv.data.network.SharedVideo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object FirestoreRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val vaultCollection = firestore.collection("community_vault")
    // Helper to get current UID
    private fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: "unknown_device"
    }


//    fun shareVideoToVault(video: VideoItem) {
//        val sharedVideo = SharedVideo(
//            id = video.id,
//            title = video.title,
//            subtitle = video.subtitle,
//            thumbnailUrl = video.thumbnailUrl,
//            likedBy = emptyList() // CHANGED: Initialize with an empty list instead of 0
//        )
//        vaultCollection.document(video.id).set(sharedVideo)
//    }

    // THE MAGIC SAUCE: Combines Share and Like into one secure operation
    fun toggleLike(video: VideoItem) {
        val uid = getCurrentUserId()
        val docRef = vaultCollection.document(video.id)

        // Run a transaction to safely read and write at the same time
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)

            if (!snapshot.exists()) {
                // If it doesn't exist, create it and add the user's UID to the list!
                val sharedVideo = SharedVideo(
                    id = video.id,
                    title = video.title,
                    subtitle = video.subtitle,
                    thumbnailUrl = video.thumbnailUrl,
                    likedBy = listOf(uid)
                )
                transaction.set(docRef, sharedVideo)
            } else {
                // If it already exists, toggle their like safely
                if (video.isLikedByMe) {
                    transaction.update(docRef, "likedBy", FieldValue.arrayRemove(uid))
                } else {
                    transaction.update(docRef, "likedBy", FieldValue.arrayUnion(uid))
                }
            }
            null // Transactions in Kotlin must return something, null is fine
        }
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
                    val currentUid = getCurrentUserId()
                    VideoItem(
                        id = shared.id,
                        title = shared.title,
                        subtitle = shared.subtitle,
                        duration = "Shared Set",
                        thumbnailUrl = shared.thumbnailUrl,
                        description = "",
                        likes = shared.likedBy.size, // Total count is the list size
                        isLikedByMe = shared.likedBy.contains(currentUid),// UI knows if this user liked it
                        sharedAt = shared.sharedAt
                    )
                } ?: emptyList()
                trySend(videos)
            }

        awaitClose { listener.remove() }
    }
    // Add this inside your FirestoreRepository object
    fun getLiveVideoFlow(baseVideo: VideoItem): Flow<VideoItem> = callbackFlow {
        val uid = getCurrentUserId()
        val docRef = vaultCollection.document(baseVideo.id)

        // Attach a real-time listener to this specific video document
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(baseVideo) // If network fails, just show the base video
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val shared = snapshot.toObject(SharedVideo::class.java)
                if (shared != null) {
                    // Combine the base video data with the LIVE like counts from Firebase
                    trySend(
                        baseVideo.copy(
                            likes = shared.likedBy.size,
                            isLikedByMe = shared.likedBy.contains(uid)
                        )
                    )
                } else {
                    trySend(baseVideo)
                }
            } else {
                // If it doesn't exist in the vault yet (e.g. fresh from Home screen), it has 0 likes
                trySend(baseVideo.copy(likes = 0, isLikedByMe = false))
            }
        }

        // Clean up the listener when the user leaves the DetailsScreen
        awaitClose { listener.remove() }
    }
}

