package com.fox.guessthecelebrities

import android.R
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.fox.guessthecelebrities.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.net.HttpURLConnection
import java.net.URL
import java.util.regex.Pattern


class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("ActivityMainBinding = null")

    private var reffs = mutableListOf<Any?>()

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        viewModel.extractStringFromHtml()

        viewModel.name.observe(this) {
            myLog(it)
            val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, it)
            binding.listView.adapter = adapter

        }
    }

    fun extractWithJsoup() {
        val startTime = System.currentTimeMillis()
        val doc = Jsoup.connect(ELLEUK).get()
//            myLog("$doc")

        doc.select("*")

            .map { col -> col.attr("href") }
            .parallelStream()
            .filter { it != null }
            .forEach { reffs.add(it) }

        reffs.forEach { myLog(it) }

        myLog("${(System.currentTimeMillis() - startTime) / 1000} seconds")

    }



    fun extractStringWithRegexFromHtml() {
        val connection = URL(ELLEUK).openConnection() as HttpURLConnection
        val data = connection.inputStream.bufferedReader().readText()


        val start =
            "<div class=\"simple-item-title item-title\">ELLE Edit: 20 Of The Best Eyeshadow Palettes</div>"

        val finish =
            "<p>Jolie is the second highest-paid actress in the world according to the magazine.</p>"


        val pattern = Regex("""$start(.*?)$finish""")
        val results = pattern.findAll(data)
        results.map {
            it.groupValues[1]
        }.toList()
    }

    fun extractStringWithRegexFromHtmlExample() {

        val data = DOC_HTML

        val start = "<body>"
        val finish = "</body>"

//        val p = Pattern.compile(start + "(.*?)" + finish)
        val p = Pattern.compile("$start(.*?)$finish")
        val m = p.matcher(data.replace("\n", ""))

        var splitContent: String? = "X"
        while (m.find()) {
            splitContent = m.group(1)
        }
        myLog(splitContent)

        val paragraphPattern = Pattern.compile("<p>" + "(.*?)" + "</p>")
        val paragraphMatcher = paragraphPattern.matcher(splitContent)
        val paragraphs = mutableListOf<String?>()
        while (paragraphMatcher.find()) {
            paragraphs.add(paragraphMatcher.group(1))
        }
        paragraphs.forEach { myLog(it) }


        val headPattern = Pattern.compile("<h1>" + "(.*?)" + "</h1>")
        val headMatcher = headPattern.matcher(splitContent)
        val headers = mutableListOf<String?>()
        while (headMatcher.find()) {
            headers.add(headMatcher.group(1))
        }
        headers.forEach { myLog(it) }

        //        val pattern = Regex("$start(.*?)$finish")
//        val results = pattern.findAll(data)
//            .map {
//                it.groupValues[1]
//            }.toList()
//            .forEach { myLog(it) }


    }

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

        private const val DOC_HTML = """<!DOCTYPE html>
            <html>
            <body>

            <h1>My First Heading</h1>
            <h1>My Second Heading</h1>
            <h1>My Third Heading</h1>
            <h1>My Fourth Heading</h1>

            <p>My first paragraph</p>
            <p>My second paragraph</p>
            <p>My third paragraph</p>
            <p>My fourth paragraph</p>

            </body>
            </html>"""
    }
}