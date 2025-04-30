package com.example.composeapp.data.enitities.quran


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Attributes(
    @SerialName("index")
    val index: String?,
    @SerialName("name")
    val name: String?
)