package com.dicoding.asclepius.view

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.data.remote.response.ArticlesItem
import com.dicoding.asclepius.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.viewModelScope

class MainViewModel: ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _news = MutableLiveData<List<ArticlesItem>>()
    val news: LiveData<List<ArticlesItem>> = _news
    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage


    init {
        showListNews()
    }

    private fun showListNews(){
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    ApiConfig.getApiService().getListNews()
                }

                _isLoading.value = false

                _news.value = response.articles


            } catch (e: Exception){
                _isLoading.value = false
                Log.e("catch", "Error: ${e.message}")
                _toastMessage.value = e.message
            }
        }

    }
}