package edu.uw.wkwok16.weparty

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import edu.uw.wkwok16.weparty.DataService.CurrentParty
import edu.uw.wkwok16.weparty.DataService.FirebasePartyDataService
import edu.uw.wkwok16.weparty.DataService.Party
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_party_detail.*

class PartyDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_party_detail)

        val ctx = this
        party_next.setOnClickListener { view ->
            try {
                val geocoder = Geocoder(ctx)
                // add party and on success start the intent with the party key
                val addressString = user_address.text.toString()
                val address = geocoder.getFromLocationName(addressString, 1)
                if (address != null && address.size > 0) {
                    val locationAsAddress = address[0]
                    val location = Location("homeLocation")
                    location.latitude = locationAsAddress.latitude
                    location.longitude = locationAsAddress.longitude
                    val partyToAdd = Party(
                        user_phone.text.toString(),
                        firstName.text.toString(),
                        party_name.text.toString(),
                        location,
                        false,
                        CurrentParty.getCurrentLocation(),
                        false
                    )
                    FirebasePartyDataService.AddParty(partyToAdd, {
                        partyId -> run {
                            CurrentParty.setPartyId(partyId)
                            val intent = Intent(this, PartyCreated::class.java)
                            intent.putExtra(PARTY_CREATED_KEY_EXTRA, partyId)
                            startActivity(intent)
                        }
                    }, {})
                } else {
                    throw Error("Failed to find address")
                }
            } catch (e:Error) {
                val failToast = Toast.makeText(applicationContext, "Address failed", Toast.LENGTH_LONG)
                failToast.show()
            }

        }

        val actionbar = supportActionBar
        actionbar!!.title = "Party Details"
        actionbar.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
