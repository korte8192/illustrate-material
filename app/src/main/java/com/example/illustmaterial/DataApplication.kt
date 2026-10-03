package com.example.illustmaterial

import android.app.Application
import java.io.File

class DataApplication :Application(){
    val datalist = DataList()

    override fun onCreate() {
        super.onCreate()

        val file=File(filesDir,"data.json")

        if(file.exists()){
            val json=file.readText()
            datalist.load(json)
        }
    }
}