package edu.uw.wkwok16.weparty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_party_detail.*

class PartyDetail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_party_detail)

        party_next.setOnClickListener { view ->
            val intent = Intent(this, PartyCreated :: class.java)
            startActivity(intent)
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
