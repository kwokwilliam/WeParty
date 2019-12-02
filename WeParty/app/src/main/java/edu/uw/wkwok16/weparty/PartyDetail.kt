package edu.uw.wkwok16.weparty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class PartyDetail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_party_detail)

        val actionbar = supportActionBar
        actionbar!!.title = "Party Details"
        actionbar.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
