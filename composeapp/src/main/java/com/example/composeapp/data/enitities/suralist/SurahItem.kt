package com.example.composeapp.data.enitities.suralist


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SurahItem(
    @SerialName("count")
    val count: Int?,
    @SerialName("index")
    val index: String?,
    @SerialName("juz")
    val juz: List<Juz?>?,
    @SerialName("pages")
    val pages: String?,
    @SerialName("place")
    val place: Place?,
    @SerialName("title")
    val title: Title?,
    @SerialName("titleAr")
    val titleAr: String?,
    @SerialName("titleTranslate")
    val titleTranslate: TitleTranslate?,
    @SerialName("type")
    val type: String?
)