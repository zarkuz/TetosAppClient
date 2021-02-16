package com.example.firebase11_loginwithnewtutor

import java.time.format.DateTimeFormatter

class Task{
    companion object Factory {
        fun create(): Task = Task()
    }

    var objectId: String? = null
    var latiApp: String? = null
    var langiApp: String? = null
    var timeY: Int? = null
    var timeM: Int? = null
    var timeD: Int? = null
    var timeH: Int? = null
    var timeMi: Int? = null
    var timeS: Int? = null

}