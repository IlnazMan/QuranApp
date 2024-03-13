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

        return jsoup.getElementsByTag("li")
            .filter {
                it.getElementsByClass("quran__sura-number quran__sura-number--sura").isNotEmpty()
            }
            .map {
                Surah(
                    it.getElementsByClass("title").text(),
                    it.getElementsByClass("href").attr("href"),
                    it.getElementsByClass("quran__sura-number quran__sura-number--sura").text()
                )
            }
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

        return jsoup.getElementsByClass("u_quran-sura__article").let { sura ->
            sura.getOrNull(0)?.getElementsByClass("u_quran-ajat")?.map {
                AyahBlock(
                    number = it.getElementsByClass("u_quran-ajat__number").text(),
                    arabic = it.getElementsByClass("u_quran-ajat__arab")[0].getElementsByClass("u_quran-ajat__arab-word")
                        .joinToString(" ") { it.text() },
                    transcription = it.getElementsByClass("u_quran-ajat__transcription").text(),
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
            }
        }.orEmpty()
    }
}