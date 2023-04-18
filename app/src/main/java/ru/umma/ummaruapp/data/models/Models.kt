package ru.umma.ummaruapp.data.models

import java.io.Serializable

data class Surah(val name: String, val link: String, val number: String): Serializable

data class AyahBlock(
    val number: String,
    val arabic: String,
    val transcription: String,
    val translate: String,
    val explanation: String
): Serializable