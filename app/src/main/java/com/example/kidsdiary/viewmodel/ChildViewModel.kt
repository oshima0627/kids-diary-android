package com.example.kidsdiary.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kidsdiary.data.db.KidsDiaryDatabase
import com.example.kidsdiary.data.model.Child
import com.example.kidsdiary.data.repository.ChildRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * 子供情報管理の ViewModel
 * ホーム画面・子供登録・編集画面で使用
 */
class ChildViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ChildRepository

    /** 全子供のリスト（StateFlow でUI に公開） */
    val children: StateFlow<List<Child>>

    init {
        val db = KidsDiaryDatabase.getDatabase(application)
        repository = ChildRepository(db.childDao())
        children = repository.getAllChildren()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    /** 子供を登録する */
    fun insertChild(child: Child) {
        viewModelScope.launch {
            repository.insertChild(child)
        }
    }

    /** 子供情報を更新する */
    fun updateChild(child: Child) {
        viewModelScope.launch {
            repository.updateChild(child)
        }
    }

    /** 子供を削除する */
    fun deleteChild(child: Child) {
        viewModelScope.launch {
            repository.deleteChild(child)
        }
    }

    /** IDで子供を取得する */
    fun getChildById(childId: Long): StateFlow<Child?> =
        repository.getChildById(childId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
