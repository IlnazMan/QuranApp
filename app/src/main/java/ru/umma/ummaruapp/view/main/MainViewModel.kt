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
    private val _surahListLiveData = MutableLiveData<MainScreenState>()
    val surahListLiveData: LiveData<MainScreenState> = _surahListLiveData

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val list = provider.getSurahList()

            val fromPref = prefs.getLastAyah()

            _surahListLiveData.postValue(MainScreenState(list, fromPref.first, fromPref.second))
        }
    }

    fun updateData() {
        val data = surahListLiveData.value ?: return

        val fromPref = prefs.getLastAyah()

        _surahListLiveData.postValue(data.copy(last = fromPref.first, lastOffset = fromPref.second))
    }
}

data class MainScreenState(
    val suraList: List<Surah>,
    val last: String? = null,
    val lastOffset: Int = 0,
)
