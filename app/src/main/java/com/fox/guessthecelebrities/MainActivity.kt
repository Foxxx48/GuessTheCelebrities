package com.fox.guessthecelebrities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.fox.guessthecelebrities.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element


class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("ActivityMainBinding = null")

    var parseItems = arrayListOf<ParseItem>()

    val wiki = "https://en.wikipedia.org"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch(Dispatchers.IO) {


            val startTime = System.currentTimeMillis()
            val doc =
                Jsoup.connect("$wiki/wiki/List_of_films_with_a_100%25_rating_on_Rotten_Tomatoes")
                    .get()
            doc.select(".wikitable:first-of-type tr td:first-of-type a")
                .map { col -> col.attr("href") }
                .parallelStream()
                .map { extractMovieData(it) }
                .filter { it != null }
                .forEach { println(it) }

            println("${(System.currentTimeMillis() - startTime) / 1000} seconds")


//            val doc = Jsoup.connect(ELLEUK).get()
////            myLog("$doc")
//            val data = doc.select("div class")
//                .map { col -> col.attr("href") }
//            myLog("$data")

//                val imgUrl = data.select("The Highest-Paid Female Celebrities Of 2020")
//                    .select("img")
//                    .eq(i)
//                    .attr("src")


//                val title = data.select("The Highest-Paid Female Celebrities Of 2020")
//                    .select("span class=\"listicle-slide-hed-text\"")
//                    .eq(i)
//                    .text()
//
//                val detailUrl = data.select("The Highest-Paid Female Celebrities Of 2020")
//                    .select("a")
//                    .eq(i)
//                    .attr("href")

//                parseItems.add(ParseItem(imgUrl))
//                parseItems.forEach{
//                    myLog(it.imageUrl)


        }


//            val connection = URL(ELLEUK).openConnection() as HttpURLConnection
//            val data = connection.inputStream.bufferedReader().readText()
//            myLog(data)
//            val start =
//                "<div class="
//            val finish = "<p>Jolie is the second highest-paid actress in the world according to the magazine.</p>"
//
//            val pattern = Pattern.compile("$start (.*?) $finish")
//            val matcher: Matcher = pattern.matcher(data)
//
//            var splitContent = "X"
//            while (matcher.find()) {
//                splitContent = matcher.group(1)?.toString() ?: "Not match"
//            }
//            myLog(splitContent)


    }

    fun extractMovieData(url: String): Movie? {
        val doc: Document
        try {
            doc = Jsoup.connect("$wiki$url").get()
        } catch (e: Exception) {
            return null

        }
        val movie = Movie()
        doc.select(".infobox tr")
            .forEach { ele ->
                when {
                    ele.getElementsByTag("th")?.hasClass("summary") ?: false -> {
                        movie.title = ele.getElementsByTag("th")?.text()
                    }
                    /*ele.getElementsByTag("img").isNotEmpty() -> {
                        movie.posterURL = "https:" + ele.getElementsByTag("img").attr("src")
                    }*/
                    else -> {
                        val value: String? = if (ele.getElementsByTag("li").size > 1)
                            ele.getElementsByTag("li").map(Element::text).filter(String::isNotEmpty).joinToString(", ") else
                            ele.getElementsByTag("td")?.first()?.text()

                        when (ele.getElementsByTag("th")?.first()?.text()) {
                            "Directed by" -> movie.directedBy = value ?: ""
                            "Produced by" -> movie.producedBy = value ?: ""
                            "Written by" -> movie.writtenBy = value ?: ""
                            "Starring" -> movie.starring = value ?: ""
                            "Music by" -> movie.musicBy = value ?: ""
                            "Release date" -> movie.releaseDate = value ?: ""
                            //"poster URL" -> movie.posterURL = value ?: ""
                            "title" -> movie.title = value ?: ""
                        }
                    }
                }
            }
        return movie
    }

//    private fun getContent() {
//        val task = DownloadContentTask()
//        try {
//            val content: String = task.execute(url).get()
//            val start = "<p class=\"link\">Topp 100 kändisar</p>"
//            val finish = "<div class=\"col-xs-12 col-sm-6 col-md-4\">"
//            val pattern = Pattern.compile("$start(.*?)$finish")
//            val matcher = pattern.matcher(content)
//            var splitContent: String? = ""
//            while (matcher.find()) {
//                splitContent = matcher.group(1)
//            }
//            val patternImg = Pattern.compile("<img src=\" + (.*?)\"")
//            val patternName = Pattern.compile("alt=\"(.*?)\"/>")
//            val matcherImg = patternImg.matcher(splitContent)
//            val matcherName = patternName.matcher(splitContent)
//            while (matcherImg.find()) {
//                urls.add(matcherImg.group(1))
//            }
//            while (matcherName.find()) {
//                names.add(matcherName.group(1))
//            }
//        } catch (e: ExecutionException) {
//            e.printStackTrace()
//        } catch (e: InterruptedException) {
//            e.printStackTrace()
//        }
//    }


        override fun onDestroy() {
            super.onDestroy()
            _binding = null
        }

        private fun myLog(message: Any?) {
            Log.d(TAG, "$message")
        }

        companion object {
            private const val TAG = "myApp"
            private const val ELLEUK =
                "https://www.elle.com/uk/life-and-culture/g32776728/richest-female-celebrities/"
        }
    }