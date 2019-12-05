package edu.uw.wkwok16.weparty

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.database.FirebaseDatabase
import com.mapbox.android.core.location.*
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import edu.uw.wkwok16.weparty.DataService.*
import kotlinx.android.synthetic.main.activity_follow_party.*

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback, PermissionsListener {
    private var permissionsManager: PermissionsManager = PermissionsManager(this)
    private lateinit var mapboxMap: MapboxMap
    private var parties: Map<PartyId, Party> = mapOf()
    private val DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L
    private val DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5
    private var callback: LocationEngineCallback<LocationEngineResult> = object:
        LocationEngineCallback<LocationEngineResult> {
        override fun onSuccess(current: LocationEngineResult){
            val theCurrentLocation = current.lastLocation
            val currentPartyId = CurrentParty.getPartyId()

            if(theCurrentLocation != null) {
                CurrentParty.setCurrentLocation(theCurrentLocation)
            }

            if(theCurrentLocation != null && currentPartyId != "" && parties.get(currentPartyId)?.homeSafe == false){
                FirebasePartyDataService.SetLiveLocation(currentPartyId, theCurrentLocation, {},{})
            }
        }

        override fun onFailure(exception: Exception){
            val failToast = Toast.makeText(applicationContext, "can't give live service", Toast.LENGTH_LONG)
            failToast.show()
        }
    }
    private var stopGettingParties: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var file = File(filesDir, "coordinates.txt")
        val checker = FileReader(file).readText()
        var result: List<String> = checker.split(",").dropLast(1)
        PointsSingleton.setKeyList(result)
        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.access_token))
        setContentView(R.layout.activity_main)
        mapView?.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        setSupportActionBar(toolbar)

        // create party button
        party_fab.setOnClickListener { view ->
            val intent = Intent(this, PartyDetail :: class.java)
            startActivity(intent)
        }

        // emergency call button
        follow_fab.setOnClickListener { view ->
            val intent = Intent(this, FollowParty :: class.java)
            startActivity(intent)
        }

        emergency_fab.setOnClickListener { view ->
            emergencyCall()
        }

        // Looping handler
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object : Runnable {
            override fun run() {
                initLocationEngine()
                mainHandler.postDelayed(this, 10000)
            }
        })

        // Getting parties
        stopGettingParties = FirebasePartyDataService.GetParties({ partiesLive ->
            parties = partiesLive

            // BUILD PUSH NOTIFICATIONS
            val newList = mutableListOf<String>()
            val list = PointsSingleton.getKeyList()
            var deletedKeysCount = 0;
            list?.forEach { key ->
                run {
                    if (parties.containsKey(key)) {
                        val party = parties.get(key)
                        if (party != null && party.homeSafe) {
                            buildNotification("${party.firstName} has made it home safe!")
//                            var file = File(filesDir, "coordinates.txt")
//                            val checker = FileReader(file).readText()
//                            var result: List<String> = checker.split(",").dropLast(1)

                            deletedKeysCount++
                        } else {
                            newList.add(key)
                        }

                        if (party != null && party.emergencyCalled) {
                            buildNotification("${party.firstName} has alerted the authorities!")
                        }
                    }
                }
            }

            if (deletedKeysCount > 0) {
                if(!filesDir.exists()){
                    filesDir.mkdirs()
                }
                val files = File(filesDir, "coordinates.txt")
                var listAsString = newList.joinToString(",")
                val writer = FileWriter(files)
                writer.write(listAsString)
                writer.close()

                PointsSingleton.setKeyList(newList)
            }
        }, {})
    }

    private fun buildNotification(message: String) {

    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(Style.Builder().fromUri(
            "mapbox://styles/mapbox/streets-v10")) {

            // Map is set up and the style has loaded. Now you can add data or make other map adjustments
            enableLocationComponent(it)
        }
    }

    /**
     * Set up the LocationEngine and the parameters for querying the device's location
     */
    @SuppressLint("MissingPermission")
    private fun initLocationEngine() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            var locationEngine = LocationEngineProvider.getBestLocationEngine(this)

            var request = LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build()
            locationEngine.requestLocationUpdates(request, callback, getMainLooper())
            locationEngine.getLastLocation(callback)
        }
    }


    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Create and customize the LocationComponent's options
            val customLocationComponentOptions = LocationComponentOptions.builder(this)
                .trackingGesturesManagement(true)
                .accuracyColor(ContextCompat.getColor(this, R.color.blue))
                .build()

            val locationComponentActivationOptions = LocationComponentActivationOptions.builder(this, loadedMapStyle)
                .locationComponentOptions(customLocationComponentOptions)
                .build()

            // Get an instance of the LocationComponent and then adjust its settings
            mapboxMap.locationComponent.apply {

                // Activate the LocationComponent with options
                activateLocationComponent(locationComponentActivationOptions)

                // Enable to make the LocationComponent visible
                isLocationComponentEnabled = true

                // Set the LocationComponent's camera mode
                cameraMode = CameraMode.TRACKING

                // Set the LocationComponent's render mode
                renderMode = RenderMode.COMPASS
            }
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        Toast.makeText(this, "user location permission explanation", Toast.LENGTH_LONG).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationComponent(mapboxMap.style!!)
        } else {
            Toast.makeText(this, "user_location_permission_not_granted", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Add the mapView lifecycle to the activity's lifecycle methods
    public override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    public override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    fun emergencyCall() {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:911")
        intent.resolveActivity(packageManager)?.let {
            startActivity(intent)
        }
    }
}
