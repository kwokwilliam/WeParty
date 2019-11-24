package edu.uw.wkwok16.weparty.DataService

import android.location.Location
import com.google.android.gms.tasks.Task
import java.util.*

interface WePartyDataService {
    /**
     * AddParty adds a party to the database. We currently use the user's phone number as their ID
     *
     * When the addition is made, the onComplete function will be called.
     */
    fun AddParty(
        party: Party,
        onComplete: OnCompleteFunction = null
    )

    /**
     * UpdateParty updates a party in the database.
     *
     * When the update is made, the onComplete function will be called.
     */
    fun UpdateParty(
        partyId: PartyId,
        updatedParty: Party,
        onComplete: OnCompleteFunction = null
    )

    /**
     * GetParties will grab parties and pass them to the function to run within a listener.
     *
     * This function gets live party data!
     */
    fun GetParties(
        functionToRun: (parties: Map<PartyId, Party>) -> Unit
    )

    /**
     * RemoveParty removes the provided party. (It does not add the party to the past parties list)
     */
    fun RemoveParty(
        partyId: PartyId,
        onComplete: OnCompleteFunction = null
    )

    /**
     * SetLiveLocation sets the live location for the specified party.
     *
     * Only use this for the CURRENT USER if they have a Party active.
     */
    fun SetLiveLocation(
        partyId: PartyId,
        userLocation: Location,
        onComplete: OnCompleteFunction = null
    )

    /**
     * SetEmergencyCalled will set the emergency called on that party
     */
    fun SetEmergencyCalled(
        partyId: PartyId,
        onComplete: OnCompleteFunction = null
    )

    /**
     * AddObserver adds the partyId to the provided user's observation list
     */
    fun AddObserver(
        partyId: PartyId,
        userPhoneNumber: String
    )

    /**
     * RemoveObserver removes the partyId from the provided user's observation list
     */
    fun RemoveObserver(
        partyId: PartyId,
        userPhoneNumber: String
    )

    /**
     * ListenToWatchLists will listen in on any watchlist changes and run the function provided when
     * a change occurs.
     */
    fun ListenToWatchLists(
        functionToRun: (userToWatchList: Map<String, WatchList>) -> Unit
    )
}