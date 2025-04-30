package com.example.composeapp.data.enitities.translation


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Ayah(
    @SerialName("note")
    val note: String?,
    @SerialName("tafsir")
    val tafsir: String?
)