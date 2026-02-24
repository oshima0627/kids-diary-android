package com.example.kidsdiary.data.repository

import com.example.kidsdiary.data.db.ChildDao
import com.example.kidsdiary.data.model.Child
import kotlinx.coroutines.flow.Flow

/**
 * 子供情報のリポジトリ
 * ViewModel から直接 DAO を参照しないように仲介する
 */
class ChildRepository(private val childDao: ChildDao) {

    /** 全ての子供を取得 */
    fun getAllChildren(): Flow<List<Child>> = childDao.getAllChildren()

    /** IDで子供を取得 */
    fun getChildById(childId: Long): Flow<Child?> = childDao.getChildById(childId)

    /** 子供を登録 */
    suspend fun insertChild(child: Child): Long = childDao.insertChild(child)

    /** 子供情報を更新 */
    suspend fun updateChild(child: Child) = childDao.updateChild(child)

    /** 子供を削除 */
    suspend fun deleteChild(child: Child) = childDao.deleteChild(child)
}
