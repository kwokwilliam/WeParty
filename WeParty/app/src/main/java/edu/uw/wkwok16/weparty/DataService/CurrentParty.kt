package edu.uw.wkwok16.weparty.DataService

import android.location.Location

class CurrentParty {
    companion object CurrentPartyId {
        private var parties: Map<PartyId, Party> = mapOf()
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

        fun setParties(partiesIn: Map<PartyId, Party>) {
            parties = partiesIn
        }

        fun getParties(): Map<PartyId, Party> {
            return parties
        }
    }
}