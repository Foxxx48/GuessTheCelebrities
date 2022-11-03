package com.fox.guessthecelebrities

class Movie {
    var title: String? = ""
    var directedBy: String = ""
    var producedBy: String = ""
    var writtenBy: String = ""
    var starring: String = ""
    var musicBy: String = ""
    var releaseDate: String = ""
    //var posterURL: String = ""

    override fun toString(): String {
        return "Movie(title='$title')"
    }

}