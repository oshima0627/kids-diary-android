package com.example.kidsdiary.data.repository

import com.example.kidsdiary.data.db.GrowthRecordDao
import com.example.kidsdiary.data.model.GrowthRecord
import kotlinx.coroutines.flow.Flow

/**
 * 成長記録のリポジトリ
 */
class GrowthRecordRepository(private val growthRecordDao: GrowthRecordDao) {

    /** 指定した子供の全成長記録を取得 */
    fun getRecordsByChildId(childId: Long): Flow<List<GrowthRecord>> =
        growthRecordDao.getRecordsByChildId(childId)

    /** 指定した子供の指定日以降の成長記録を取得（グラフ用） */
    fun getRecordsSince(childId: Long, fromDate: Long): Flow<List<GrowthRecord>> =
        growthRecordDao.getRecordsSince(childId, fromDate)

    /** 指定した子供の最新の成長記録を取得 */
    suspend fun getLatestRecord(childId: Long): GrowthRecord? =
        growthRecordDao.getLatestRecord(childId)

    /** 成長記録を登録 */
    suspend fun insertRecord(record: GrowthRecord): Long =
        growthRecordDao.insertRecord(record)

    /** 成長記録を更新 */
    suspend fun updateRecord(record: GrowthRecord) =
        growthRecordDao.updateRecord(record)

    /** 成長記録を削除 */
    suspend fun deleteRecord(record: GrowthRecord) =
        growthRecordDao.deleteRecord(record)
}
