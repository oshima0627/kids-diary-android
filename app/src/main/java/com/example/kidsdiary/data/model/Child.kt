package com.example.kidsdiary.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 子供の基本情報を管理するエンティティ
 */
@Entity(tableName = "children")
data class Child(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    /** 子供の名前 */
    val name: String,
    /** 生年月日（エポックミリ秒） */
    val birthDate: Long,
    /** 性別: "male" or "female" or "other" */
    val gender: String,
    /** アイコン写真のURIパス（nullable） */
    val iconUri: String? = null,
    /** 登録日時（エポックミリ秒） */
    val createdAt: Long = System.currentTimeMillis()
)
