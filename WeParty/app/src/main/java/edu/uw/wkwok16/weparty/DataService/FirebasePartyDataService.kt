package edu.uw.wkwok16.weparty.DataService

import android.location.Location
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.HashMap

const val ERR_COULD_NOT_GET_KEY = "Could not get key"

class FirebasePartyDataService {
    companion object DataService: WePartyDataService {
        private val db = FirebaseDatabase.getInstance()

        override fun AddParty(party: Party, onSuccess: ((partyId: PartyId) -> Unit), onFailure: () -> Unit) {
            val ref = db.getReference(USER_PARTIES)
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
            val ref = db.getReference(USER_PARTIES)
            val partyValues = updatedParty.toMap()
            val childUpdates = HashMap<String, Any>()
            childUpdates[partyId] = partyValues
            ref.updateChildren(childUpdates).addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener {
                onFailure()
            }
        }

        fun UpdateNothing (
            partyId: PartyId,
            party: Party
        ) {
            val ref = db.getReference(USER_PARTIES)
            val childUpdates = HashMap<String, Any>()
            childUpdates[partyId] = party.toMap()
            ref.updateChildren(childUpdates)
        }

        override fun GetParties(
            functionToRun: (parties: Map<PartyId, Party>) -> Unit,
            onFailure: () -> Unit
        ): () -> Unit {
            val ref = db.getReference(USER_PARTIES)
            val listener = object : ValueEventListener {
                override fun onDataChange(snap: DataSnapshot) {
                    val parties: MutableMap<PartyId, Party> = mutableMapOf()
                    for (partySnap in snap.children) {
                        val partyId = partySnap.key as PartyId
                        val party = DeserializeParty(partySnap)
                        parties.set(partyId, party)
                    }
                    functionToRun(parties)
                }

                override fun onCancelled(dbError: DatabaseError) {
                    onFailure()
                }
            }

            ref.addValueEventListener(listener)

            return fun () {
                ref.removeEventListener(listener)
            }
        }

        override fun RemoveParty(partyId: PartyId, onSuccess: () -> Unit, onFailure: () -> Unit) {
            val ref = db.getReference(specificUserParty(partyId))
            val childUpdates = HashMap<String, Any>()
            childUpdates[Party.HOME_SAFE] = true
            ref.updateChildren(childUpdates).addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener {
                onFailure()
            }
        }

        override fun SetLiveLocation(
            partyId: PartyId,
            userLocation: Location,
            onSuccess: () -> Unit,
            onFailure: () -> Unit
        ) {
            Log.i("location", userLocation.toString())
            val ref = db.getReference(specificUserParty(partyId))
            val childUpdates = HashMap<String, Any>()
            childUpdates[Party.LIVE_LOCATION] = mapOf(
                Party.LATITUDE to userLocation.latitude,
                Party.LONGITUDE to userLocation.longitude
            )
            ref.updateChildren(childUpdates).addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener {
                onFailure()
            }
        }

        override fun SetEmergencyCalled(
            partyId: PartyId,
            onSuccess: () -> Unit,
            onFailure: () -> Unit
        ) {
            if (partyId != "") {
                val ref = db.getReference(specificUserParty(partyId))
                Log.i("TEST", specificUserParty(partyId))

                val childUpdates = HashMap<String, Any>()
                childUpdates[Party.EMERGENCY_CALLED] = true
                ref.updateChildren(childUpdates).addOnSuccessListener {
                    onSuccess()
                }.addOnFailureListener {
                    onFailure()
                }
            }
        }

        const val USER_PARTIES = "UserParties/"

        private fun specificUserParty(partyKey: PartyId): String {
            return USER_PARTIES + partyKey
        }

        private fun DeserializeParty(snap: DataSnapshot): Party {
            val homeLocation = Location(Party.HOME_LOCATION)
            homeLocation.latitude = snap.child(Party.HOME_LOCATION).child(Party.LATITUDE).value as Double
            homeLocation.longitude = snap.child(Party.HOME_LOCATION).child(Party.LONGITUDE).value as Double

            val liveLocation = Location(Party.LIVE_LOCATION)
            liveLocation.latitude = snap.child(Party.LIVE_LOCATION).child(Party.LATITUDE).value as Double
            liveLocation.longitude = snap.child(Party.LIVE_LOCATION).child(Party.LONGITUDE).value as Double

            return Party(
                snap.child(Party.USER_PHONE_NUMBER).value as String,
                snap.child(Party.USER_NAME).value as String,
                snap.child(Party.NAME).value as String,
                homeLocation,
                snap.child(Party.EMERGENCY_CALLED).value as Boolean,
                liveLocation,
                snap.child(Party.HOME_SAFE).value as Boolean
            )
        }
    }
}
