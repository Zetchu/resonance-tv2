package edu.rit.dk9612.resonancetv.data.repository

import edu.rit.dk9612.resonancetv.data.model.VideoCategory
import edu.rit.dk9612.resonancetv.data.model.VideoItem
import edu.rit.dk9612.resonancetv.data.network.RetrofitInstance
import edu.rit.dk9612.resonancetv.data.network.YouTubeItem // <--- IMPORT THIS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

open class YouTubeRepository {

    private val api = RetrofitInstance.api

    open suspend fun getHomeCategories(): List<VideoCategory> = withContext(Dispatchers.IO) {
        try {
            val technoResponse = api.searchVideos(query = "Industrial Techno DJ Set")
            val boilerRoomResponse = api.searchVideos(query = "Boiler Room Set")

            val technoVideos = technoResponse.items.mapNotNull { mapToVideoItem(it) }
            val boilerRoomVideos = boilerRoomResponse.items.mapNotNull { mapToVideoItem(it) }

            listOf(
                VideoCategory("Trending Techno Sets", technoVideos),
                VideoCategory("Recent Boiler Rooms", boilerRoomVideos)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // UPDATED: We now pass the strictly typed YouTubeItem instead of 'Any'
    private fun mapToVideoItem(item: YouTubeItem): VideoItem? {
        // Safe call: If videoId is null, we return null and mapNotNull skips it!
        val validId = item.id.videoId ?: return null

        return VideoItem(
            id = validId,
            title = item.snippet.title,
            subtitle = item.snippet.channelTitle,
            duration = "Live Set",
            thumbnailUrl = item.snippet.thumbnails.high.url,
            description = item.snippet.description
        )
    }

    suspend fun searchByGenre(genre: String): List<VideoItem> = withContext(Dispatchers.IO) {
        try {
            // We append "DJ Set" to ensure we get music results
            val response = api.searchVideos(query = "$genre DJ Set")
            response.items.mapNotNull { item ->
                val validId = item.id.videoId ?: return@mapNotNull null
                VideoItem(
                    id = validId,
                    title = item.snippet.title,
                    subtitle = item.snippet.channelTitle,
                    duration = "Live Set",
                    thumbnailUrl = item.snippet.thumbnails.high.url,
                    description = item.snippet.description
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}