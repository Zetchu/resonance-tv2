package edu.rit.dk9612.resonancetv.ui

import androidx.lifecycle.ViewModel
import edu.rit.dk9612.resonancetv.data.model.VideoItem

import edu.rit.dk9612.resonancetv.data.repository.FirestoreRepository
import kotlinx.coroutines.flow.Flow

class CommunityViewModel : ViewModel() {

    // UI collects this state, instead of talking to Firestore directly
    val communityVideosFlow: Flow<List<VideoItem>> = FirestoreRepository.getCommunityVideosFlow()

    // Wraps the live video fetch
    fun getLiveVideoFlow(baseVideo: VideoItem): Flow<VideoItem> {
        return FirestoreRepository.getLiveVideoFlow(baseVideo)
    }

    // Wraps the like toggle
    fun toggleLike(video: VideoItem) {
        FirestoreRepository.toggleLike(video)
    }
}