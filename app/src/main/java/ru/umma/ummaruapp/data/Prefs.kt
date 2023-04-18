package ru.umma.ummaruapp.data

import android.content.Context

class Prefs(private val context: Context) {
    private companion object {
        const val PREFS_KEY = "prefs_tafsir"
        const val KEY_AYAH = "key_ayah"
    }

    private val _prefs by lazy { context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE) }

    fun saveLastAyah(ayahNumber: String) {
        _prefs.edit().putString(KEY_AYAH, ayahNumber).commit()
    }

    fun getLastAyah() = _prefs.getString(KEY_AYAH, null)
}