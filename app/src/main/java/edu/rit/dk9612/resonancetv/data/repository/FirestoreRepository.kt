package edu.rit.dk9612.resonancetv.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.rit.dk9612.resonancetv.data.model.VideoItem
import edu.rit.dk9612.resonancetv.data.model.SharedVideo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object FirestoreRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val vaultCollection = firestore.collection("community_vault")
    private fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: "unknown_device"
    }


    fun toggleLike(video: VideoItem) {
        val uid = getCurrentUserId()
        val docRef = vaultCollection.document(video.id)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)

            if (!snapshot.exists()) {
                val sharedVideo = SharedVideo(
                    id = video.id,
                    title = video.title,
                    subtitle = video.subtitle,
                    thumbnailUrl = video.thumbnailUrl,
                    likedBy = listOf(uid)
                )
                transaction.set(docRef, sharedVideo)
            } else {
                if (video.isLikedByMe) {
                    transaction.update(docRef, "likedBy", FieldValue.arrayRemove(uid))
                } else {
                    transaction.update(docRef, "likedBy", FieldValue.arrayUnion(uid))
                }
            }
            null
        }
    }
    fun getCommunityVideosFlow(): Flow<List<VideoItem>> = callbackFlow {
        val listener = vaultCollection
            .orderBy("sharedAt", Query.Direction.DESCENDING)
            .limit(50)
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
                        likes = shared.likedBy.size,
                        isLikedByMe = shared.likedBy.contains(currentUid),
                        sharedAt = shared.sharedAt
                    )
                } ?: emptyList()
                trySend(videos)
            }

        awaitClose { listener.remove() }
    }
    fun getLiveVideoFlow(baseVideo: VideoItem): Flow<VideoItem> = callbackFlow {
        val uid = getCurrentUserId()
        val docRef = vaultCollection.document(baseVideo.id)

        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(baseVideo)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val shared = snapshot.toObject(SharedVideo::class.java)
                if (shared != null) {
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
                trySend(baseVideo.copy(likes = 0, isLikedByMe = false))
            }
        }
        awaitClose { listener.remove() }
    }
}

