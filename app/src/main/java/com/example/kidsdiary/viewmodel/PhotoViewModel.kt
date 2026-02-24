package com.example.kidsdiary.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kidsdiary.data.db.KidsDiaryDatabase
import com.example.kidsdiary.data.model.Photo
import com.example.kidsdiary.data.repository.PhotoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * 写真管理の ViewModel
 * 写真アルバム画面・写真追加画面で使用
 */
class PhotoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PhotoRepository

    init {
        val db = KidsDiaryDatabase.getDatabase(application)
        repository = PhotoRepository(db.photoDao())
    }

    /** 指定した子供の全写真を取得 */
    fun getPhotosByChildId(childId: Long): StateFlow<List<Photo>> =
        repository.getPhotosByChildId(childId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** 指定した年月の写真を取得（アルバム表示用） */
    fun getPhotosByMonth(childId: Long, year: Int, month: Int): StateFlow<List<Photo>> {
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, 1, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val monthStart = cal.timeInMillis

        cal.add(Calendar.MONTH, 1)
        val monthEnd = cal.timeInMillis

        return repository.getPhotosByMonth(childId, monthStart, monthEnd)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    /** 写真を登録する */
    fun insertPhoto(photo: Photo) {
        viewModelScope.launch {
            repository.insertPhoto(photo)
        }
    }

    /** 写真情報を更新する */
    fun updatePhoto(photo: Photo) {
        viewModelScope.launch {
            repository.updatePhoto(photo)
        }
    }

    /** 写真を削除する */
    fun deletePhoto(photo: Photo) {
        viewModelScope.launch {
            repository.deletePhoto(photo)
        }
    }
}
