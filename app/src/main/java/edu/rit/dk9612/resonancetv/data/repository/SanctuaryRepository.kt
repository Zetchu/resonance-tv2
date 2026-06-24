package edu.rit.dk9612.resonancetv.data.repository

import androidx.lifecycle.LiveData
import edu.rit.dk9612.resonancetv.data.local.AppDatabase
import edu.rit.dk9612.resonancetv.data.local.VideoDAO
import edu.rit.dk9612.resonancetv.data.local.VideoEntity

class SanctuaryRepository(private val videoDao: VideoDAO) {

    val allSavedVideos: LiveData<List<VideoEntity>> = videoDao.getSavedVideos()

    fun insert(video: VideoEntity) {
        AppDatabase.databaseWriteExecutor.execute {
            videoDao.insert(video)
        }
    }

    fun delete(video: VideoEntity) {
        AppDatabase.databaseWriteExecutor.execute {
            videoDao.delete(video)
        }
    }
}