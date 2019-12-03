package edu.uw.wkwok16.weparty.DataService

import android.location.Location
import com.google.android.gms.tasks.Task
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class Party(
    val userPhoneNumber: String,
    val name: String,
    val homeLocation: Location,
    val partyLocation: Location,
    val timeStart: Date,
    val timeEnd: Date,
    val userLocation: Location,
    val emergencyCalled: Boolean,
    val liveLocation: Location,
    val past: Boolean
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userPhoneNumber" to userPhoneNumber,
            "name" to name,
            "homeLocation" to mapOf(
                "lat" to homeLocation.latitude,
                "lng" to homeLocation.longitude
            ),
            "partyLocation" to mapOf(
                "lat" to partyLocation.latitude,
                "lng" to partyLocation.longitude
            ),
            "timeStart" to timeStart.time,
            "timeEnd" to timeEnd.time,
            "userLocation" to mapOf(
                "lat" to userLocation.latitude,
                "lng" to userLocation.longitude
            ),
            "emergencyCalled" to emergencyCalled,
            "liveLocation" to mapOf(
                "lat" to liveLocation.latitude,
                "lng" to liveLocation.longitude
            )
        )
    }
}

typealias PartyId = String
typealias WatchList = Map<PartyId, Boolean>