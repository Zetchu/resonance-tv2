package edu.rit.dk9612.resonancetv.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete

@Dao
interface VideoDAO {
    @Query("SELECT * FROM sanctuary_table ORDER BY title ASC")
    fun getSavedVideos(): LiveData<List<VideoEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(video: VideoEntity)

    @Delete
    fun delete(video: VideoEntity)
}