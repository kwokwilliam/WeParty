package edu.uw.wkwok16.weparty.DataService

import android.location.Location
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

const val ERR_COULD_NOT_GET_KEY = "Could not get key"

class FirebasePartyDataService: WePartyDataService {
    private val db = FirebaseDatabase.getInstance()

    override fun AddParty(party: Party, onSuccess: ((partyId: PartyId) -> Unit), onFailure: () -> Unit) {
        val ref = db.getReference(ACTIVE_USER_PARTIES)
        val key = ref.push().key ?: throw Error(ERR_COULD_NOT_GET_KEY)
        val partyValues = party.toMap()
        val childUpdates = HashMap<String, Any>()
        childUpdates[key] = partyValues
        ref.updateChildren(childUpdates).addOnSuccessListener {
            onSuccess(key)
        }.addOnFailureListener {
            onFailure()
        }
    }

    override fun UpdateParty(
        partyId: PartyId,
        updatedParty: Party,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val ref = db.getReference(ACTIVE_USER_PARTIES)
        val partyValues = updatedParty.toMap()
        val childUpdates = HashMap<String, Any>()
        childUpdates[partyId] = partyValues
        ref.updateChildren(childUpdates).addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onFailure()
        }
    }

    override fun GetParties(
        functionToRun: (parties: Map<PartyId, Party>) -> Unit,
        onFailure: () -> Unit
    ): () -> Unit {
        val ref = db.getReference(ACTIVE_USER_PARTIES)
        val listener = object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {

            }

            override fun onCancelled(dbError: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        ref.addListenerForSingleValueEvent(listener)

        return fun () {}
    }

    override fun RemoveParty(partyId: PartyId, onSuccess: () -> Unit, onFailure: () -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun SetLiveLocation(
        partyId: PartyId,
        userLocation: Location,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun SetEmergencyCalled(
        partyId: PartyId,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun AddUser(
        phoneNumber: String,
        name: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun UpdateUser(
        phoneNumber: String,
        name: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object Paths {
        const val BASE = ""
        const val USER_IDS_TO_NAME = "UserIDsToName/"
        const val ACTIVE_WATCH_LIST = "ActiveWatchList/"
        const val ACTIVE_USER_PARTIES = "ActiveUserParties/"
        const val PAST_WATCH_LIST = "PastWatchList/"
        const val PAST_USER_PARTIES = "PastUserParties/"

        fun specificUserIdToName(userId: String): String {
            return USER_IDS_TO_NAME + userId
        }

        fun specificActiveWatchlist(userId: String): String {
            return ACTIVE_WATCH_LIST + userId
        }

        fun specificActiveUserParty(partyKey: PartyId): String {
            return ACTIVE_USER_PARTIES + partyKey
        }

        fun specificPastWatchlist(userId: String): String {
            return PAST_WATCH_LIST + userId
        }

        fun specificPastUserParty(partyKey: PartyId): String {
            return PAST_USER_PARTIES + partyKey
        }
    }
}
