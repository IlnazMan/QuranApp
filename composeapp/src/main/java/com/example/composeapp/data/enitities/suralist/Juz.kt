package com.example.composeapp.data.enitities.suralist


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Juz(
    @SerialName("index")
    val index: String?,
    @SerialName("verse")
    val verse: Verse?
)