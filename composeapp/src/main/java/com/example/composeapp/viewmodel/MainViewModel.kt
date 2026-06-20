package com.example.composeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeapp.data.enitities.suralist.SurahItem
import com.example.composeapp.domain.usecase.GetSurahListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getSurahListUseCase: GetSurahListUseCase
) : ViewModel() {
    private val _surahList = MutableStateFlow<List<SurahItem>>(emptyList())
    val surahList: StateFlow<List<SurahItem>> = _surahList

    init {
        loadSurahList()
    }

    private fun loadSurahList() {
        viewModelScope.launch {
            _surahList.value = getSurahListUseCase()
        }
    }
}
