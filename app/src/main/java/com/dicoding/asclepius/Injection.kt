package com.dicoding.asclepius

import android.content.Context
import com.dicoding.asclepius.data.HistoryRepository
import com.dicoding.asclepius.data.local.room.HistoryDatabase

object Injection {
    fun provideRepository(context: Context): HistoryRepository {
        val database = HistoryDatabase.getInstance(context)
        val dao = database.historyDao()
        return HistoryRepository.getInstance(dao)
    }
}