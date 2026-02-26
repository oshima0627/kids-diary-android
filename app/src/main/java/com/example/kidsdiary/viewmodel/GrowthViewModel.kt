package com.example.kidsdiary.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kidsdiary.data.db.KidsDiaryDatabase
import com.example.kidsdiary.data.model.GrowthRecord
import com.example.kidsdiary.data.repository.GrowthRecordRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

/**
 * 成長記録管理の ViewModel
 * 成長記録一覧・グラフ・入力画面で使用
 */
class GrowthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GrowthRecordRepository

    /** 期間フィルターの種類 */
    enum class PeriodFilter {
        THREE_MONTHS,   // 3ヶ月
        SIX_MONTHS,     // 6ヶ月
        ONE_YEAR,       // 1年
        ALL             // 全期間
    }

    private val _periodFilter = MutableStateFlow(PeriodFilter.ALL)

    /** 現在選択中の期間フィルター */
    val periodFilter: StateFlow<PeriodFilter> = _periodFilter

    init {
        val db = KidsDiaryDatabase.getDatabase(application)
        repository = GrowthRecordRepository(db.growthRecordDao())
    }

    /** 指定した子供の全成長記録を取得 */
    fun getRecordsByChildId(childId: Long): StateFlow<List<GrowthRecord>> =
        repository.getRecordsByChildId(childId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** グラフ用: 現在の期間フィルターに基づいた成長記録を取得 */
    fun getFilteredRecords(childId: Long): Flow<List<GrowthRecord>> {
        return _periodFilter.flatMapLatest { filter ->
            val fromDate = when (filter) {
                PeriodFilter.THREE_MONTHS -> getDateBefore(3)
                PeriodFilter.SIX_MONTHS -> getDateBefore(6)
                PeriodFilter.ONE_YEAR -> getDateBefore(12)
                PeriodFilter.ALL -> 0L
            }
            repository.getRecordsSince(childId, fromDate)
        }
    }

    /** 期間フィルターを変更する */
    fun setPeriodFilter(filter: PeriodFilter) {
        _periodFilter.value = filter
    }

    /** 成長記録を登録する */
    fun insertRecord(record: GrowthRecord) {
        viewModelScope.launch {
            repository.insertRecord(record)
        }
    }

    /** 成長記録を更新する */
    fun updateRecord(record: GrowthRecord) {
        viewModelScope.launch {
            repository.updateRecord(record)
        }
    }

    /** 成長記録を削除する */
    fun deleteRecord(record: GrowthRecord) {
        viewModelScope.launch {
            repository.deleteRecord(record)
        }
    }

    /** 指定した子供の最新成長記録を取得する */
    suspend fun getLatestRecord(childId: Long): GrowthRecord? =
        repository.getLatestRecord(childId)

    /** 現在から指定した月数前のUTC深夜0時のエポックミリ秒を返す */
    private fun getDateBefore(months: Int): Long {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.add(Calendar.MONTH, -months)
        return cal.timeInMillis
    }
}
