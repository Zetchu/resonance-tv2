package edu.rit.dk9612.resonancetv.ui

import androidx.lifecycle.ViewModel
import edu.rit.dk9612.resonancetv.data.model.VideoItem

import edu.rit.dk9612.resonancetv.data.repository.FirestoreRepository
import kotlinx.coroutines.flow.Flow

class CommunityViewModel : ViewModel() {

    val communityVideosFlow: Flow<List<VideoItem>> = FirestoreRepository.getCommunityVideosFlow()
    fun getLiveVideoFlow(baseVideo: VideoItem): Flow<VideoItem> {
        return FirestoreRepository.getLiveVideoFlow(baseVideo)
    }
    fun toggleLike(video: VideoItem) {
        FirestoreRepository.toggleLike(video)
    }
}