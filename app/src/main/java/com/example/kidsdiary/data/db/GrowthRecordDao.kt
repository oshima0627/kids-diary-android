package com.example.kidsdiary.data.db

import androidx.room.*
import com.example.kidsdiary.data.model.GrowthRecord
import kotlinx.coroutines.flow.Flow

/**
 * 成長記録のDAOインターフェース
 */
@Dao
interface GrowthRecordDao {

    /** 指定した子供の全成長記録を日付降順で取得（Flow でリアルタイム監視） */
    @Query("SELECT * FROM growth_records WHERE childId = :childId ORDER BY date DESC")
    fun getRecordsByChildId(childId: Long): Flow<List<GrowthRecord>>

    /** 指定した子供の指定期間の成長記録を日付昇順で取得（グラフ用） */
    @Query("SELECT * FROM growth_records WHERE childId = :childId AND date >= :fromDate ORDER BY date ASC")
    fun getRecordsSince(childId: Long, fromDate: Long): Flow<List<GrowthRecord>>

    /** 指定した子供の最新の成長記録を1件取得 */
    @Query("SELECT * FROM growth_records WHERE childId = :childId ORDER BY date DESC LIMIT 1")
    suspend fun getLatestRecord(childId: Long): GrowthRecord?

    /** 成長記録を登録 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: GrowthRecord): Long

    /** 成長記録を更新 */
    @Update
    suspend fun updateRecord(record: GrowthRecord)

    /** 成長記録を削除 */
    @Delete
    suspend fun deleteRecord(record: GrowthRecord)
}
