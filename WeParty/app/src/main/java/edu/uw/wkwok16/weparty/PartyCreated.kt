package edu.uw.wkwok16.weparty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_party_created.*

const val PARTY_CREATED_KEY_EXTRA = "PARTY_CREATED_KEY_EXTRA"

class PartyCreated : AppCompatActivity() {
    var partyKey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_party_created)

        partyKey = intent.getStringExtra(PARTY_CREATED_KEY_EXTRA) ?: "ERROR"

        val actionbar = supportActionBar
        actionbar!!.title = "Back to Home"
        actionbar.setDisplayHomeAsUpEnabled(true)

        party_id.setText(partyKey)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
