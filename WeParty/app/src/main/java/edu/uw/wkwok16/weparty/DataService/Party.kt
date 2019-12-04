package edu.uw.wkwok16.weparty.DataService

import android.location.Location
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Party(
    val userPhoneNumber: String,
    val firstName: String,
    val partyName: String,
    val homeLocation: Location,
    val emergencyCalled: Boolean,
    val liveLocation: Location,
    val homeSafe: Boolean
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            USER_PHONE_NUMBER to userPhoneNumber,
            USER_NAME to firstName,
            NAME to partyName,
            HOME_LOCATION to mapOf(
                LATITUDE to homeLocation.latitude,
                LONGITUDE to homeLocation.longitude
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
        const val USER_NAME = "userName"
        const val NAME = "name"
        const val HOME_LOCATION = "homeLocation"
        const val EMERGENCY_CALLED = "emergencyCalled"
        const val LIVE_LOCATION = "liveLocation"
        const val HOME_SAFE = "homeSafe"
    }
}

typealias PartyId = String