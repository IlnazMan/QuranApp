package com.example.composeapp.data.enitities.quran

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @author i.m.mannapov
 */
@Serializable
data class QuranData(
    @SerialName("sura")
    val suraList: List<SuraData>,
)