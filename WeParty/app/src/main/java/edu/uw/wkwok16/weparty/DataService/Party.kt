package edu.uw.wkwok16.weparty.DataService

import android.location.Location
import com.google.android.gms.tasks.Task
import java.util.*

data class Party(
    val userPhoneNumber: String,
    val name: String,
    val homeLocation: Location,
    val partyLocation: Location,
    val timeStart: Date,
    val timeEnd: Date,
    val userLocation: Location
)

typealias PartyId = String
typealias OnCompleteFunction = ((task: Task<Void>) -> Unit)?
typealias WatchList = Map<PartyId, Boolean>