package edu.uw.wkwok16.weparty

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

        val sms_text = "I'm going out to a party! Here's my party ID: \n\n" + party_id.text

        party_share.setOnClickListener { view ->
            val sendIntent = Intent(Intent.ACTION_VIEW)
            sendIntent.data = Uri.parse("sms:3605189341")
            sendIntent.putExtra("sms_body", sms_text)
            startActivity(sendIntent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
