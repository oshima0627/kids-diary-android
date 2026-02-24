package com.example.kidsdiary.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 写真記録を管理するエンティティ
 */
@Entity(
    tableName = "photos",
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
data class Photo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    /** 対象の子供ID */
    val childId: Long,
    /** 写真の日付（エポックミリ秒） */
    val date: Long,
    /** 写真ファイルのURIパス */
    val uri: String,
    /** コメント */
    val comment: String? = null,
    /** 登録日時（エポックミリ秒） */
    val createdAt: Long = System.currentTimeMillis()
)
