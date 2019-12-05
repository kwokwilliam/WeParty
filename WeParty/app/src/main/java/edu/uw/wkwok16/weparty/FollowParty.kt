package edu.uw.wkwok16.weparty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.uw.wkwok16.weparty.DataService.*
import kotlinx.android.synthetic.main.activity_follow_party.*
import java.io.File
import java.io.FileReader
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
            var currentCode = input_code.text.toString() + ","

            if (this.files != null) {
                val checker = FileReader(files).readText()
                val writer = FileWriter(files)
                writer.write(checker + currentCode)
                writer.close()
            }

            val currList: List<String>? = PointsSingleton.getKeyList()
            val newList: MutableList<String> = mutableListOf()
            if (currList != null) {
                currList.forEach { partyId ->
                    run {
                        newList.add(partyId)
                    }
                }
            }
            newList.add(input_code.text.toString())
            val parties = CurrentParty.getParties()

            FirebasePartyDataService.UpdateNothing(
                "a",
                parties.get(input_code.text.toString()) as Party
            )
            PointsSingleton.setKeyList(newList)

            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
