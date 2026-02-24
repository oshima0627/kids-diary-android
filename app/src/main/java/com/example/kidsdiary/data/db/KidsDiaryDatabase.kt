package com.example.kidsdiary.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kidsdiary.data.model.Child
import com.example.kidsdiary.data.model.GrowthRecord
import com.example.kidsdiary.data.model.Photo

/**
 * アプリのメインデータベース
 * Room を使ったローカルストレージ
 */
@Database(
    entities = [Child::class, GrowthRecord::class, Photo::class],
    version = 1,
    exportSchema = false
)
abstract class KidsDiaryDatabase : RoomDatabase() {

    abstract fun childDao(): ChildDao
    abstract fun growthRecordDao(): GrowthRecordDao
    abstract fun photoDao(): PhotoDao

    companion object {
        @Volatile
        private var INSTANCE: KidsDiaryDatabase? = null

        fun getDatabase(context: Context): KidsDiaryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KidsDiaryDatabase::class.java,
                    "kids_diary_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
