package com.example.composeapp.data.enitities.suralist


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Title(
    @SerialName("rus")
    val rus: String?,
    @SerialName("tat")
    val tat: String?
)