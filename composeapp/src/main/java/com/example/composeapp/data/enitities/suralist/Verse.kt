package com.example.composeapp.data.enitities.suralist


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Verse(
    @SerialName("end")
    val end: String?,
    @SerialName("start")
    val start: String?
)