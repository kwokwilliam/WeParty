package edu.uw.wkwok16.weparty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class FollowParty : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_follow_party)

        val actionbar = supportActionBar
        actionbar!!.title = "Follow Party"
        actionbar.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
