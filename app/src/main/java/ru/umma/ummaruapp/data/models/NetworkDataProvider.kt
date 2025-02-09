package ru.umma.ummaruapp.data.models

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jsoup.Jsoup
import ru.umma.ummaruapp.data.db.Ayahs
import ru.umma.ummaruapp.data.db.Database
import ru.umma.ummaruapp.data.db.LastDownload
import ru.umma.ummaruapp.data.db.Surahs
import ru.umma.ummaruapp.domain.DataProvider

const val BASE_URL = "https://umma.ru"

class NetworkDataProvider(private val _db: Database) : DataProvider {
    private fun getSurahList(): List<Surah> {
        val jsoup = Jsoup.connect("$BASE_URL/perevod-korana/")
            .followRedirects(true)
            .maxBodySize(90_000_000)
            .get()

        return jsoup.getElementsByClass("PerevodKorana_link-box__Izyjj")
            .filter { it.getElementsByClass("SuraBlock_block__J7Yuo").isNotEmpty() }.get(5)
            .children()
            .map {
                val nameArab =
                    it.getElementsByClass("Title_title__CWCK0 SuraBlock_surah-text__6WS1A").text()
                val nameRus = it.getElementsByClass("SubTitle_subtitle__B6BP3").text()
                Surah(
                    name = "$nameArab ($nameRus)",
                    link = it.getElementsByAttribute("href").attr("href"),
                    number = it.getElementsByClass("Counter_counter__pYK7i").text(),
                )
            }
            .filter { it.number.isNotEmpty() }
    }

    override val downloadProgress: MutableStateFlow<Pair<Int, Int>> = MutableStateFlow(0 to 0)

    override suspend fun downloadAll() {
        val suraList = getSurahList()
        downloadProgress.value = 0 to suraList.size

        _db.holyQuranDao().insertSurahs(
            suraList.mapIndexed { index, surah ->

                val s = Surahs(
                    surah.name,
                    surah.link,
                    surah.number
                )

                val ayahs = getSurahContent(surah)

                _db.holyQuranDao().insertAyahs(
                    ayahs.map {
                        Ayahs(
                            it.number,
                            it.arabic,
                            it.transcription,
                            it.translate,
                            it.explanation,
                            s.number
                        )
                    }
                )

                println("!!! ${index} to ${suraList.size}")
                downloadProgress.value = index to suraList.size

                s
            }
        )
        _db.holyQuranDao().insertLastDownload(LastDownload(System.currentTimeMillis()))
    }

    private fun getSurahContent(surah: Surah): List<AyahBlock> {
        val jsoup = Jsoup.connect("${BASE_URL}${surah.link}")
            .followRedirects(true)
            .maxBodySize(90_000_000)
            .get()

        return jsoup.getElementsByClass("SuraPageContainer_surah-container__bSb7s").getOrNull(0)
            ?.getElementsByClass("AyatWithTranslate_ayat-box__cHgM8")
            ?.map {
                AyahBlock(
                    number = it.id().orEmpty(),
                    arabic = it.getElementsByClass("AyatWithTranslate_ayat-text__ZmHEw")[0].children()
                        .joinToString(separator = " ") { it.text() },
                    transcription = it.getElementsByClass("AyatTranscription_ayat-transcription__text__A15_e")
                        .text(),
                    translate = it.getElementsByClass("u_quran-ajat__translate")
                        .flatMap { it.children() }
                        .filter { !it.hasClass("explanation explanation--quran") }
                        .joinToString("\n") {
                            it.text()
                        },
                    it.getElementsByClass("explanation explanation--quran")
                        .flatMap { it.children() }
                        .joinToString("\n") {
                            "${if (it.elementSiblingIndex() > 0) "[${it.elementSiblingIndex()}] " else ""}${
                                it.text().filter { it != '*' }
                            }"
                        }
                )
            }.orEmpty()
    }
}