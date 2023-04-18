package ru.umma.ummaruapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.umma.ummaruapp.data.Prefs
import ru.umma.ummaruapp.data.models.Surah
import ru.umma.ummaruapp.domain.IQuranProvider

class MainViewModel(
    private val provider: IQuranProvider,
    private val prefs: Prefs
) : ViewModel() {
    private val _surahListLiveData = MutableLiveData<Pair<List<Surah>, String?>>()
    val surahListLiveData: LiveData<Pair<List<Surah>, String?>> = _surahListLiveData

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val list = provider.getSurahList()

            _surahListLiveData.postValue(list to prefs.getLastAyah())
        }
    }

    fun checkLastAyah() {
        val (lst, toLast) = _surahListLiveData.value ?: return

        val fromPref = prefs.getLastAyah()

        if (toLast != fromPref) {
            _surahListLiveData.value = (lst to fromPref)
        }
    }
}