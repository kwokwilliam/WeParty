package edu.uw.wkwok16.weparty.DataService

import android.location.Location

class CurrentParty {
    companion object CurrentPartyId {
        private var currentParty = ""
        private var location = Location("")

        fun setPartyId(partyId: PartyId) {
            currentParty = partyId
        }

        fun getPartyId (): PartyId {
            return currentParty
        }

        fun getCurrentLocation(): Location {
            return location
        }

        fun setCurrentLocation(loc: Location) {
            location = loc
        }
    }
}