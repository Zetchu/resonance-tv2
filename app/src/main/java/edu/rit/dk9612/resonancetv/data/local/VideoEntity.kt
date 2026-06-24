package edu.rit.dk9612.resonancetv.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sanctuary_table")
data class VideoEntity(
    @PrimaryKey(autoGenerate = false)
    var id: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "subtitle")
    val subtitle: String,

    @ColumnInfo(name = "duration")
    val duration: String
)