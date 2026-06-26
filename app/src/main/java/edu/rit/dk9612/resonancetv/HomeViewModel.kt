package edu.rit.dk9612.resonancetv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.rit.dk9612.resonancetv.BuildConfig
import edu.rit.dk9612.resonancetv.data.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    // You will need to get a free API key from the Google Cloud Console
    private val YOUTUBE_API_KEY = BuildConfig.YOUTUBE_API_KEY

    private val _uiState = MutableStateFlow<List<VideoCategory>>(emptyList())
    val uiState: StateFlow<List<VideoCategory>> = _uiState

    init {
        fetchHomeContent()
    }

    private fun fetchHomeContent() {
        viewModelScope.launch {
            try {
                // Fetch two different categories in parallel
                val technoResponse = RetrofitInstance.api.searchVideos(query = "Industrial Techno DJ Set", apiKey = YOUTUBE_API_KEY)
                val boilerRoomResponse = RetrofitInstance.api.searchVideos(query = "Boiler Room Set", apiKey = YOUTUBE_API_KEY)

                // Map the network data to our UI VideoItem model
                val technoVideos = technoResponse.items.mapNotNull { item ->
                    item.id.videoId?.let { validId ->
                        VideoItem(
                            id = validId,
                            title = item.snippet.title,
                            subtitle = item.snippet.channelTitle,
                            duration = "Live Set",
                            thumbnailUrl = item.snippet.thumbnails.high.url,
                            description = item.snippet.description
                        )
                    }
                }

                val boilerRoomVideos = boilerRoomResponse.items.mapNotNull { item ->
                    item.id.videoId?.let { validId ->
                        VideoItem(
                            id = validId,
                            title = item.snippet.title,
                            subtitle = item.snippet.channelTitle,
                            duration = "Live Set",
                            thumbnailUrl = item.snippet.thumbnails.high.url,
                            description = item.snippet.description
                        )
                    }
                }

                // Push the real data to the UI!
                _uiState.value = listOf(
                    VideoCategory("Trending Techno Sets", technoVideos),
                    VideoCategory("Recent Boiler Rooms", boilerRoomVideos)
                )

            } catch (e: Exception) {
                // Handle no internet or API errors here
                e.printStackTrace()
            }
        }
    }
}