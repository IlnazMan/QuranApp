package com.example.composeapp.data.enitities.quran


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttributesX(
    @SerialName("bismillah")
    val bismillah: String? = null,
    @SerialName("index")
    val index: String?,
    @SerialName("text")
    val text: String?
)