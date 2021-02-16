package com.example.firebase11_loginwithnewtutor

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var latiApp: String? = null
    private var langiApp: String? = null
    private var add1: Double? = 0.0
    private var add2: Double? = 0.0
    private lateinit var fusedLoationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        if (intent.extras != null){
            val terima = intent.extras
            latiApp = terima?.getString("latiApp")
            langiApp = terima?.getString("langiApp")
        }else{
            latiApp = "0.0"
            langiApp = "0.0"
        }

        fusedLoationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        add1 = latiApp?.toDouble()
        add2 = langiApp?.toDouble()
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.isMyLocationEnabled = true

        val lokasiku = LatLng(add1!!, add2!!)
        mMap.addMarker(MarkerOptions().position(lokasiku).title("Lokasi Terakhir Anda"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasiku, 12f))
    }
}
