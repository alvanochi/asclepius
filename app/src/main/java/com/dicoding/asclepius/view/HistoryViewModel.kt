package com.dicoding.asclepius.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.data.HistoryRepository
import com.dicoding.asclepius.data.local.entity.HistoryEntity
import kotlinx.coroutines.launch

class HistoryViewModel(private val historyRepository: HistoryRepository) : ViewModel() {
    fun getListHistory() = historyRepository.getListHistory()

    fun setListHistory(histories: List<HistoryEntity>) {
        viewModelScope.launch {
            historyRepository.setListHistory(histories)
        }
    }
}