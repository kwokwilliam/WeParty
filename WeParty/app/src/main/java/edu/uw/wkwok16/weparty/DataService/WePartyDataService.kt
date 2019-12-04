package edu.uw.wkwok16.weparty.DataService

import android.location.Location

interface WePartyDataService {
    /**
     * AddParty adds a party to the database. We currently use the user's phone number as their ID
     *
     * When the addition is made, the onSuccess function will be called.
     */
    fun AddParty(
        party: Party,
        onSuccess: ((partyId: PartyId) -> Unit),
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
     * GetParties will grab parties and pass them to the function to run within a listener.
     *
     * This function gets live party data!
     *
     * It returns a function that when called will stop the event listener.
     */
    fun GetParties(
        functionToRun: (parties: Map<PartyId, Party>) -> Unit,
        onFailure: () -> Unit
    ): () -> Unit

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
}