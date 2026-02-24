package com.example.kidsdiary.data.db

import androidx.room.*
import com.example.kidsdiary.data.model.Photo
import kotlinx.coroutines.flow.Flow

/**
 * 写真記録のDAOインターフェース
 */
@Dao
interface PhotoDao {

    /** 指定した子供の全写真を日付降順で取得（Flow でリアルタイム監視） */
    @Query("SELECT * FROM photos WHERE childId = :childId ORDER BY date DESC")
    fun getPhotosByChildId(childId: Long): Flow<List<Photo>>

    /** 指定した子供の指定年月の写真を取得（アルバム表示用） */
    @Query("""
        SELECT * FROM photos
        WHERE childId = :childId
        AND date >= :monthStart
        AND date < :monthEnd
        ORDER BY date ASC
    """)
    fun getPhotosByMonth(childId: Long, monthStart: Long, monthEnd: Long): Flow<List<Photo>>

    /** 写真を登録 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: Photo): Long

    /** 写真情報を更新 */
    @Update
    suspend fun updatePhoto(photo: Photo)

    /** 写真を削除 */
    @Delete
    suspend fun deletePhoto(photo: Photo)
}
