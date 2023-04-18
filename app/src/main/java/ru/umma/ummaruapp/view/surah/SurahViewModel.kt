package ru.umma.ummaruapp.view.surah

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.umma.ummaruapp.data.Prefs
import ru.umma.ummaruapp.data.models.AyahBlock
import ru.umma.ummaruapp.data.models.Surah
import ru.umma.ummaruapp.domain.IQuranProvider

class SurahViewModel(
    private val surah: Surah,
    private val provider: IQuranProvider,
    private val prefs: Prefs
) : ViewModel() {
    private val _surahListLiveData = MutableLiveData<Pair<List<AyahBlock>, String?>>()
    val surahListLiveData: LiveData<Pair<List<AyahBlock>, String?>> = _surahListLiveData

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val list = provider.getSurahContent(surah)

            _surahListLiveData.postValue(list to prefs.getLastAyah())
        }
    }

    fun saveLastAyah(ayah: String) {
        prefs.saveLastAyah(ayah)
    }
}