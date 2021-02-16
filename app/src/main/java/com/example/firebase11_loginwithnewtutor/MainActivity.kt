package com.example.firebase11_loginwithnewtutor

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.snapshot.ChildKey
import com.google.firebase.database.snapshot.KeyIndex
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDateTime

class MainActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener   {
    //Firebase references
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    private var lastLocatation: FirebaseDatabase? = null
    //UI elements
    private var tvFirstName: TextView? = null
    private var tvLastName: TextView? = null
    private var tvEmail: TextView? = null
    private var tvEmailVerifiied: TextView? = null
    private var btnLogout: Button?= null
    private var btnOpeartion: Button?= null
    private var btnLastLoc: Button?= null

    //Dataku
    private var status: Int? =0
    private var langiAppsen: String? = "0.0"
    private var latiAppsen: String? = "0.0"





    //GeoLocation
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocation: Location? = null
    private var mLocationManager: LocationManager? = null
    private var mLocationRequest: LocationRequest? = null
    private val UPDATE_INTERVAL = (2 * 1000).toLong()  /* 10 secs */
    private val FASTEST_INTERVAL: Long = 5000 /* 2 sec */
    private var locationManager: LocationManager? = null
    lateinit var _db: DatabaseReference
    private val isLocationEnabled: Boolean
        get() {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager!!.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER)
        }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialise()

        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()

        mLocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        checkLocation() //check whether location service is enable or not in your  phone

        _db = FirebaseDatabase.getInstance().reference
    }
    private fun initialise() {
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")
        mAuth = FirebaseAuth.getInstance()
        tvFirstName = findViewById<View>(R.id.tv_first_name) as TextView
        tvLastName = findViewById<View>(R.id.tv_last_name) as TextView
        tvEmail = findViewById<View>(R.id.tv_email) as TextView
        tvEmailVerifiied = findViewById<View>(R.id.tv_email_verifiied) as TextView
        btnLastLoc = findViewById<View>(R.id.btn_lastloc) as Button
        btnLogout = findViewById<View>(R.id.btn_logout) as Button
        btnOpeartion = findViewById<View>(R.id.btn_operation) as Button
        btnOpeartion!!.text = "Mulai"
        btnLogout!!.setOnClickListener { createbtnLogout() }
        btnOpeartion!!.setOnClickListener { createbtnOperation() }
        btnLastLoc!!.setOnClickListener { createLastLoc() }


    }
    private fun createLastLoc(){
        val valueKu1 = latiAppsen?.toDouble()
        val valueKu2 = langiAppsen?.toString()
        val pindahMap = Intent(this, MapsActivity::class.java)
        val datasen = Bundle()
        datasen.putString("latiApp", langiAppsen!!)
        datasen.putString("langiApp", latiAppsen!!)
        pindahMap.putExtras(datasen)
        startActivity(pindahMap)
    }
    private fun createbtnLogout(){
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
    }
    private fun createbtnOperation(){
        if (status==1){
            status = 0
            btnOpeartion!!.text = "Mulai"
//            btnOpeartion!!.setBackgroundColor(119911)
        } else {
            status = 1
            btnOpeartion!!.text = "Berhenti"
//            btnOpeartion!!.setBackgroundColor(991111)
        }
    }

    override fun onStart() {
        super.onStart()
        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
        mDatabaseReference!!.child(mUser!!.uid).child("lokasiku").orderByKey().limitToLast(1).addListenerForSingleValueEvent(listenerkuy)

        tvEmail!!.text = mUser.email
        tvEmailVerifiied!!.text = mUser.isEmailVerified.toString()

        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tvFirstName!!.text = snapshot.child("nik").value.toString()
                tvLastName!!.text = snapshot.child("nama").value as String
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
        if (mGoogleApiClient != null) {
            mGoogleApiClient!!.connect()
        }
    }

    var listenerkuy: ValueEventListener= object : ValueEventListener{
        override fun onDataChange(snap: DataSnapshot) {
            val valku = snap!!.children
            valku.forEach{
                val dataku = it.key.toString()
                langiAppsen = snap.child(dataku).child("langiApp").value.toString()
                latiAppsen = snap.child(dataku).child("latiApp").value.toString()
                btnLastLoc!!.text = "Lokasi di temukan"
            }
        }
        override fun onCancelled(databaseError: DatabaseError) {}
    }
    fun checkPermission(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),0)
            }
            return
        }
    }


    //GeoLocationScript

    @SuppressLint("MissingPermission")
    override fun onConnected(p0: Bundle?) {
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermission()
        }
        startLocationUpdates()

            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)

            if (mLocation == null) {
                startLocationUpdates()
            }
            if (mLocation != null) {

                // mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
                //mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));
            } else {
                Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show()
            }

    }

    override fun onConnectionSuspended(i: Int) {
        Log.i(TAG, "Connection Suspended")
        mGoogleApiClient!!.connect()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode())
    }



    override fun onStop() {
        super.onStop()
        if (mGoogleApiClient!!.isConnected()) {
            mGoogleApiClient!!.disconnect()
        }
    }

    @SuppressLint("MissingPermission")
    protected fun startLocationUpdates() {
        // Create the location request

            mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL)
            // Request location updates
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                checkPermission()
                return
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest, this
            )
            Log.d("reque", "--->>>>")

    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onLocationChanged(location: Location) {
        if (status==1) {
            val lokasilat = location.latitude
            val lokasilong = location.longitude

            val msg = "Updated Location: " +
                    java.lang.Double.toString(location.latitude) + "," +
                    java.lang.Double.toString(location.longitude)
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            // You can now create a LatLng Object for use with maps
            pushFb(lokasilat, lokasilong)
            latiAppsen = lokasilong.toString()
            langiAppsen = lokasilat.toString()

        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun pushFb(lokasilat: Double, lokasilong: Double) {
        val mUser1 = mAuth!!.currentUser
        val currentYear = LocalDateTime.now().year
        val currentMonth = LocalDateTime.now().monthValue
        val currentDay = LocalDateTime.now().dayOfMonth
        val currentTimeH = LocalDateTime.now().hour
        val currentTimeM = LocalDateTime.now().minute
        val currentTimeS = LocalDateTime.now().second

        val task = Task.create()
        task.langiApp= lokasilat.toString()
        task.latiApp= lokasilong.toString()
        task.timeY = currentYear
        task.timeM = currentMonth
        task.timeD = currentDay
        task.timeH = currentTimeH
        task.timeMi = currentTimeM
        task.timeS = currentTimeS

        val newTask = _db.child(Statics.FIREBASE_TASK).child(mUser1!!.uid).child("lokasiku").push()
        task.objectId = newTask.key
        newTask.setValue(task)
    }

    private fun checkLocation(): Boolean {
        if (!isLocationEnabled)
            showAlert()
        return isLocationEnabled
    }

    private fun showAlert() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Enable Location")
            .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " + "use this app")
            .setPositiveButton("Location Settings") { paramDialogInterface, paramInt ->
                val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(myIntent)
            }
            .setNegativeButton("Cancel") { paramDialogInterface, paramInt -> }
        dialog.show()
    }

    companion object {

        private val TAG = "MainActivity"
    }

}
