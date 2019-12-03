package edu.uw.wkwok16.weparty.DataService

import android.location.Location
import com.google.android.gms.tasks.Task
import java.util.*

interface WePartyDataService {
    /**
     * AddParty adds a party to the database. We currently use the user's phone number as their ID
     *
     * When the addition is made, the onSuccess function will be called.
     */
    fun AddParty(
        party: Party,
        onSuccess: ((partyKey: PartyId) -> Unit),
        onFailure: () -> Unit
    )

    /**
     * UpdateParty updates a party in the database.
     *
     * When the update is made, the onSuccess function will be called.
     */
    fun UpdateParty(
        partyId: PartyId,
        updatedParty: Party,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    )

    /**
     * GetActiveParties will grab active parties and pass them to the function to run within a listener.
     *
     * This function gets live party data!
     */
    fun GetActiveParties(
        functionToRun: (parties: Map<PartyId, Party>) -> Unit,
        onFailure: () -> Unit
    )

    /**
     * GetPastParties will grab past parties and pass them to the function to run within a listener
     */
    fun GetPastParties(
        functionToRun: (parties: Map<PartyId, Party>) -> Unit,
        onFailure: () -> Unit
    )

    /**
     * RemoveParty removes the provided party. (It does not add the party to the past parties list)
     */
    fun RemoveParty(
        partyId: PartyId,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    )

    /**
     * SetLiveLocation sets the live location for the specified party.
     *
     * Only use this for the CURRENT USER if they have a Party active.
     */
    fun SetLiveLocation(
        partyId: PartyId,
        userLocation: Location,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    )

    /**
     * SetEmergencyCalled will set the emergency called on that party
     */
    fun SetEmergencyCalled(
        partyId: PartyId,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    )

    /**
     * AddObserver adds the partyId to the provided user's observation list
     */
    fun AddObserver(
        partyId: PartyId,
        userPhoneNumber: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    )

    /**
     * RemoveObserver removes the partyId from the provided user's observation list
     */
    fun RemoveObserver(
        partyId: PartyId,
        userPhoneNumber: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    )

    /**
     * ListenToWatchLists will listen in on any watchlist changes and run the function provided when
     * a change occurs.
     */
    fun ListenToWatchLists(
        functionToRun: (userToWatchList: Map<String, WatchList>) -> Unit,
        onFailure: () -> Unit
    )

    /**
     * Adds a user to the database via their phone number as their ID
     */
    fun AddUser(
        phoneNumber: String,
        name: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    )

    /**
     * Updates a user in the database via their phone number
     */
    fun UpdateUser(
        phoneNumber: String,
        name: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    )
}