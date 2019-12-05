package edu.uw.wkwok16.weparty

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
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
import com.google.android.gms.common.util.Strings
import com.google.firebase.database.FirebaseDatabase
import com.mapbox.android.core.location.*
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import edu.uw.wkwok16.weparty.DataService.*
import kotlinx.android.synthetic.main.activity_follow_party.*

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback, PermissionsListener {

  private val SOURCE_ID = "SOURCE_ID";
  private val ICON_ID = "ICON_ID";
  private val LAYER_ID = "LAYER_ID";
    private var permissionsManager: PermissionsManager = PermissionsManager(this)
    private lateinit var mapboxMap: MapboxMap

    private val DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L
    private val DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5
    private var symbolLayerIconFeatureList = mutableListOf<Feature>()
    private var callback: LocationEngineCallback<LocationEngineResult> = object:
        LocationEngineCallback<LocationEngineResult> {
        override fun onSuccess(current: LocationEngineResult){
            val theCurrentLocation = current.lastLocation
            val currentPartyId = CurrentParty.getPartyId()

            if(theCurrentLocation != null) {
                CurrentParty.setCurrentLocation(theCurrentLocation)
            }

            if(theCurrentLocation != null && currentPartyId != "" && CurrentParty.getParties().get(currentPartyId)?.homeSafe == false){
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

        if (!filesDir.exists()) {
            filesDir.mkdirs()
        }
        var file = File(filesDir, "coordinates.txt")
        if (file.exists()) {
            val checker = FileReader(file).readText()
            var result: List<String> = checker.split(",").dropLast(1)
            PointsSingleton.setKeyList(result)
        } else {
            file.createNewFile()
        }
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
            CurrentParty.setParties(partiesLive)
            val confirmedList = findMatchingKeys()
            var newList: MutableList<String> = mutableListOf()
            var toDelete = ""
            var deletedKeysCount = 0
            symbolLayerIconFeatureList = mutableListOf<Feature>()
            for(current in confirmedList){
                val currentParty = CurrentParty.getParties().get(current)
                val currentLocation = currentParty!!.liveLocation
                symbolLayerIconFeatureList.add(Feature.fromGeometry(
                    Point.fromLngLat(currentLocation.longitude, currentLocation.latitude)))

                if(currentParty.homeSafe) {
                    buildNotification("${currentParty.firstName} has made it home safe!")
                    toDelete = current
                    deletedKeysCount++
                } else if (currentParty.emergencyCalled) {
                    buildNotification("${currentParty.firstName} has alerted the authorities!")
                    toDelete = current
                    deletedKeysCount++
                } else {
                    newList.add(current)
                }

            }
            mapboxMap.setStyle(Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41")

                // Add the SymbolLayer icon image to the map style
               .withImage(ICON_ID, getDrawable(android.R.drawable.btn_star)!!)

                // Adding a GeoJson source for the SymbolLayer icons.
                .withSource( GeoJsonSource(SOURCE_ID,
                    FeatureCollection.fromFeatures(symbolLayerIconFeatureList))
                )

                // Adding the actual SymbolLayer to the map style. An offset is added that the bottom of the red
                // marker icon gets fixed to the coordinate, rather than the middle of the icon being fixed to
                // the coordinate point. This is offset is not always needed and is dependent on the image
                // that you use for the SymbolLayer icon.
                .withLayer(
                    SymbolLayer(LAYER_ID, SOURCE_ID)
                .withProperties(
                    PropertyFactory.iconImage(ICON_ID),
                    iconAllowOverlap(true),
                    iconIgnorePlacement(true)
            )
            ))

            if (deletedKeysCount > 0) {
                if (!filesDir.exists()) {
                    filesDir.mkdirs()
                }
                val files = File(filesDir, "coordinates.txt")
                var listAsString = newList.joinToString(",")
                val writer = FileWriter(files)
                writer.write(listAsString)
                writer.close()

                PointsSingleton.setKeyList(newList)
                FirebasePartyDataService.UpdateNothing(
                    "",
                    CurrentParty.getParties().get(toDelete) as Party // This should never be null
                )
            }



        }, {})
    }

    fun buildNotification(string: String) {

    }

   private fun findMatchingKeys():MutableList<String>{
        var confirmedPartyIds = mutableListOf<String>()
        val partyIds = PointsSingleton.getKeyList()
        val partyKeys = CurrentParty.getParties().keys
        if(partyIds != null){
            for (current in partyIds){
                if(partyKeys.contains(current as PartyId)){
                    confirmedPartyIds.add(current)
                }
            }
        }

       return confirmedPartyIds
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
