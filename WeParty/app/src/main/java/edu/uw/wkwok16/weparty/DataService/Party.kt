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
    val homeSafe: Boolean
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            USER_PHONE_NUMBER to userPhoneNumber,
            NAME to name,
            HOME_LOCATION to mapOf(
                LATITUDE to homeLocation.latitude,
                LONGITUDE to homeLocation.longitude
            ),
            PARTY_LOCATION to mapOf(
                LATITUDE to partyLocation.latitude,
                LONGITUDE to partyLocation.longitude
            ),
            TIME_START to timeStart.time,
            TIME_END to timeEnd.time,
            USER_LOCATION to mapOf(
                LATITUDE to userLocation.latitude,
                LONGITUDE to userLocation.longitude
            ),
            EMERGENCY_CALLED to emergencyCalled,
            LIVE_LOCATION to mapOf(
                LATITUDE to liveLocation.latitude,
                LONGITUDE to liveLocation.longitude
            ),
            HOME_SAFE to homeSafe
        )
    }

    companion object PartyDataConstants {
        const val LATITUDE = "lat"
        const val LONGITUDE = "lng"
        const val USER_PHONE_NUMBER = "userPhoneNumber"
        const val NAME = "name"
        const val HOME_LOCATION = "homeLocation"
        const val PARTY_LOCATION = "partyLocation"
        const val TIME_START = "timeStart"
        const val TIME_END = "timeEnd"
        const val USER_LOCATION = "userLocation"
        const val EMERGENCY_CALLED = "emergencyCalled"
        const val LIVE_LOCATION = "liveLocation"
        const val HOME_SAFE = "homeSafe"
    }
}

typealias PartyId = String