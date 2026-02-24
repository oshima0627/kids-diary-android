package com.example.kidsdiary.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 身長・体重の成長記録を管理するエンティティ
 */
@Entity(
    tableName = "growth_records",
    foreignKeys = [
        ForeignKey(
            entity = Child::class,
            parentColumns = ["id"],
            childColumns = ["childId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("childId")]
)
data class GrowthRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    /** 対象の子供ID */
    val childId: Long,
    /** 記録日付（エポックミリ秒） */
    val date: Long,
    /** 身長（cm）、null の場合は未入力 */
    val heightCm: Float? = null,
    /** 体重（kg）、null の場合は未入力 */
    val weightKg: Float? = null,
    /** メモ */
    val note: String? = null,
    /** 登録日時（エポックミリ秒） */
    val createdAt: Long = System.currentTimeMillis()
)
