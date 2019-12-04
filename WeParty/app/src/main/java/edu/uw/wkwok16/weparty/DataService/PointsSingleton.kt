package edu.uw.wkwok16.weparty.DataService

import android.location.Location
import java.io.FileReader

class PointsSingleton {
    companion object CurrentPoints {
        private var currentParty = ""
        private var location = Location("")

        fun parseListofIds(){
           var commaSeperated =  FileReader("coordinates.txt").readText()
            println(commaSeperated)
        }

        fun setPartyId(partyId: PartyId) {
            currentParty = partyId
        }

        fun getPartyId (): PartyId {
            return currentParty
        }


    }
}