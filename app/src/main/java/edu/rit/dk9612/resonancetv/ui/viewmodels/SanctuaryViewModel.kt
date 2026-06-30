package edu.rit.dk9612.resonancetv.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import edu.rit.dk9612.resonancetv.data.local.AppDatabase
import edu.rit.dk9612.resonancetv.data.local.VideoEntity
import edu.rit.dk9612.resonancetv.data.model.VideoItem
import edu.rit.dk9612.resonancetv.data.repository.SanctuaryRepository

class SanctuaryViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = SanctuaryRepository(database.videoDao())

    val savedVideos: LiveData<List<VideoEntity>> = repository.allSavedVideos

    fun addVideo(video: VideoItem) {
        repository.insert(toEntity(video))
    }

    fun removeVideo(video: VideoItem) {
        repository.delete(toEntity(video))
    }
    private fun toEntity(video: VideoItem) = VideoEntity(
        id = video.id,
        title = video.title,
        subtitle = video.subtitle,
        duration = video.duration,
        thumbnailUrl = video.thumbnailUrl
    )
}