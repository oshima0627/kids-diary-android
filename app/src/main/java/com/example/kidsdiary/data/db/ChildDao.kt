package com.example.kidsdiary.data.db

import androidx.room.*
import com.example.kidsdiary.data.model.Child
import kotlinx.coroutines.flow.Flow

/**
 * 子供情報のDAOインターフェース
 */
@Dao
interface ChildDao {

    /** 全ての子供を登録日時順で取得（Flow でリアルタイム監視） */
    @Query("SELECT * FROM children ORDER BY createdAt ASC")
    fun getAllChildren(): Flow<List<Child>>

    /** IDで子供を取得 */
    @Query("SELECT * FROM children WHERE id = :childId")
    fun getChildById(childId: Long): Flow<Child?>

    /** 子供を登録 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChild(child: Child): Long

    /** 子供情報を更新 */
    @Update
    suspend fun updateChild(child: Child)

    /** 子供を削除 */
    @Delete
    suspend fun deleteChild(child: Child)
}
