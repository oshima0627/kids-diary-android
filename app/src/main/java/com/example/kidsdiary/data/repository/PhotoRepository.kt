package com.example.kidsdiary.data.repository

import com.example.kidsdiary.data.db.PhotoDao
import com.example.kidsdiary.data.model.Photo
import kotlinx.coroutines.flow.Flow

/**
 * 写真記録のリポジトリ
 */
class PhotoRepository(private val photoDao: PhotoDao) {

    /** 指定した子供の全写真を取得 */
    fun getPhotosByChildId(childId: Long): Flow<List<Photo>> =
        photoDao.getPhotosByChildId(childId)

    /** 指定した子供の指定年月の写真を取得 */
    fun getPhotosByMonth(childId: Long, monthStart: Long, monthEnd: Long): Flow<List<Photo>> =
        photoDao.getPhotosByMonth(childId, monthStart, monthEnd)

    /** 写真を登録 */
    suspend fun insertPhoto(photo: Photo): Long = photoDao.insertPhoto(photo)

    /** 写真情報を更新 */
    suspend fun updatePhoto(photo: Photo) = photoDao.updatePhoto(photo)

    /** 写真を削除 */
    suspend fun deletePhoto(photo: Photo) = photoDao.deletePhoto(photo)
}
