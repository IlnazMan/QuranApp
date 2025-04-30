package com.example.composeapp.data.enitities.quran


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Aya(
    @SerialName("_attributes")
    val attributes: AttributesX?
)