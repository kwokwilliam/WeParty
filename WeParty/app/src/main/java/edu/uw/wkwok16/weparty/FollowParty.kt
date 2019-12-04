package edu.uw.wkwok16.weparty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_follow_party.*
import java.io.File
import java.io.FileWriter

class FollowParty : AppCompatActivity() {
    var files: File? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_follow_party)

        val actionbar = supportActionBar
        actionbar!!.title = "Follow Party"
        actionbar.setDisplayHomeAsUpEnabled(true)
        party_follow.setOnClickListener {
            if(!filesDir.exists()){
                filesDir.mkdirs()
            }
            val files = File(filesDir, "coordinates.txt")
            this.files = files
            var currentCode = input_code.text.toString() + ", "

            if (this.files != null) {
                val writer = FileWriter(this.files as File)
                writer.write(currentCode)
                writer.close()
            }
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
