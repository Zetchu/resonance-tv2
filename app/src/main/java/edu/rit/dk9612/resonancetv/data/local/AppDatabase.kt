package edu.rit.dk9612.resonancetv.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.util.concurrent.Executors

@Database(entities = [VideoEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun videoDao(): VideoDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // As required by the course architecture
        val databaseWriteExecutor = Executors.newFixedThreadPool(2)

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "resonance_database"
                ).build()
                INSTANCE = db
                db
            }
        }
    }
}