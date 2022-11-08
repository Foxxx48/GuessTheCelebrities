package com.fox.guessthecelebrities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
import java.util.regex.Pattern

class MainViewModel : ViewModel() {

    private val namesOfCeleb = mutableListOf<String?>()
    private var namCel = listOf<String>()

    private val _name = MutableLiveData<List<String?>>()
    val name: LiveData<List<String?>>
        get() = _name

    fun extractStringFromHtml() {
        viewModelScope.launch(Dispatchers.IO) {
            val connection = URL(ELLEUK).openConnection() as HttpURLConnection
            val data = connection.inputStream.bufferedReader().readText()
//            myLog(data)

            val start =
                "<div class=\"simple-item-title item-title\">ELLE Edit: 20 Of The Best Eyeshadow Palettes</div>"
            val finish =
                "<p>Jolie is the second highest-paid actress in the world according to the magazine.</p>"

            val pattern = Pattern.compile("$start(.*?)$finish")
            val matcher = pattern.matcher(data.replace("\n", ""))

            var splitContent: String? = "X"
            while (matcher.find()) {
                splitContent = matcher.group(1)
            }
//        myLog(splitContent)

            val namePattern =
                Pattern.compile("<span class=\"listicle-slide-hed-text\">" + "(.*?)" + "</span>")
            val nameMatcher = namePattern.matcher(splitContent)

            while (nameMatcher.find()) {
                namesOfCeleb.add(nameMatcher.group(1))
            }

            namCel = namesOfCeleb.map { it?.replaceAfterLast(" ", "") ?: "None" }

            _name.postValue(namCel)

//        val headPattern = Pattern.compile("<h1>" + "(.*?)" + "</h1>")
//        val headMatcher = headPattern.matcher(splitContent)
//        val headers = mutableListOf<String?>()
//        while (headMatcher.find()) {
//            headers.add(headMatcher.group(1))
//        }
//        headers.forEach { myLog(it) }
        }
    }


    companion object {
        private const val ELLEUK =
            "https://www.elle.com/uk/life-and-culture/g32776728/richest-female-celebrities/"
    }
}