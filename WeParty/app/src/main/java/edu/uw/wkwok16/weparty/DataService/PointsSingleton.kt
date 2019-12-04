package edu.uw.wkwok16.weparty.DataService

import android.content.Context
import android.location.Location
import java.io.File
import java.io.FileReader

class PointsSingleton {
    companion object CurrentPoints {
        private var listOfIds: List<String>? = null

        fun setPartyId(newList: List<String>?) {
            listOfIds = newList
        }

        fun getKeyList (): List<String>? {
            return listOfIds
        }


    }
}