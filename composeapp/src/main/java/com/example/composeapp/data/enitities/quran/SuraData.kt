package com.example.composeapp.data.enitities.quran


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SuraData(
    @SerialName("_attributes")
    val attributes: Attributes?,
    @SerialName("aya")
    val aya: List<Aya>?
)