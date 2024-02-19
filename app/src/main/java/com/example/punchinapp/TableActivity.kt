package com.example.punchinapp

import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.punchinapp.data.User
import com.example.punchinapp.data.UserViewModel
import java.util.Calendar
import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.Location
import android.widget.ScrollView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class TableActivity : AppCompatActivity() {
    private lateinit var punchIn : Button
    private lateinit var punchOut : Button
    private lateinit var userViewModel: UserViewModel
    private lateinit var table : TableLayout
    private lateinit var latitude : String
    private lateinit var longitude : String
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastLocation: Location? = null
    private lateinit var googleSigninClient: GoogleSignInClient
    private lateinit var signOutBtn: Button
    private lateinit var auth : FirebaseAuth
    private lateinit var scrollview: ScrollView
    private lateinit var switchButton : MaterialSwitch
    private var lastEntry = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table)

        window.statusBarColor = ContextCompat.getColor(this, R.color.buttonEnabled)

        punchIn = findViewById(R.id.punchInButton)
        punchOut = findViewById(R.id.punchOutButton)
        table = findViewById(R.id.tableLayout)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        auth = FirebaseAuth.getInstance()
        signOutBtn = findViewById(R.id.SignOutButton)
        scrollview = findViewById(R.id.table_scrollview)
        switchButton = findViewById(R.id.switch_btn)


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()


        googleSigninClient = GoogleSignIn.getClient(this, gso)


        if(SharedPref.isPunchInEnbled()){
            buttonEnabled(punchIn)
            buttonDisabled(punchOut)
        }
        else{
            buttonEnabled(punchOut)
            buttonDisabled(punchIn)
        }

        latitude = "0"
        longitude = "0"

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)





//        userViewModel.deleteEntries()

        getLastLocation()

        userViewModel.getLastId().observe(this){
            if(it!=null) lastEntry = it
        }

        userViewModel.readEntries(auth.currentUser?.displayName.toString()).observe(this){
            while (table.childCount > 1) {
                table.removeView(table.getChildAt(table.childCount - 1))
            }

            for(i in it){

                val tr = TableRow(this)
                val tv0 = TextView(this)
                tv0.setText(i.name)
                tv0.setPadding(10,10,10,10)
                tr.addView(tv0)

                val tv1 = TextView(this)
                tv1.setText(i.date)
                tv1.setPadding(10,10,10,10)
                tr.addView(tv1)

                val tv2 = TextView(this)
                tv2.setText(i.punchInTime)
                tv2.setPadding(10,10,10,10)
                tr.addView(tv2)

                val tv3 = TextView(this)
                tv3.setText(i.punchInLoc)
                tv3.setPadding(10,10,10,10)
                tr.addView(tv3)

                val tv4 = TextView(this)
                tv4.setText(i.punchOutTime)
                tv4.setPadding(10,10,10,10)
                tr.addView(tv4)

                val tv5 = TextView(this)
                tv5.setText(i.punchOutLoc)
                tv5.setPadding(10,10,10,10)
                tr.addView(tv5)

                tr.setPadding(10,10,10,10)
                table.addView(tr)
            }
        }


//        scrollview.fullScroll(View.FOCUS_DOWN)




        signOutBtn.setOnClickListener {
            SharedPref.setLoggedIn(SharedPref.IS_USER_LOGGED_IN,false)
            googleSigninClient.revokeAccess()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }



        punchIn.setOnClickListener{
            SharedPref.setPunchInEnabled(false)

            getLastLocation()
            checkLocationPermission()



            if (checkLocationPermission()) {
                getLastLocation()
//                while(latitude=="0" && longitude=="0"){
                    if (calculateDistance(latitude.toDouble(), longitude.toDouble()) < 0.05) {

                        val calendar = Calendar.getInstance()
                        val punchInTime = SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(calendar.time)
//                        val date = Date().toString().subSequence(0, 11).toString()
//                        val punchInTime =
                        val date = SimpleDateFormat.getDateInstance().format(calendar.time)
//                            "${calendar.get(Calendar.HOUR)} : ${calendar.get(Calendar.MINUTE)}"
                        val punchInLoc = "${latitude}\n${longitude}"



                        userViewModel.addEntry(
                            User(
                                0,
                                auth.currentUser?.displayName.toString(),
                                date,
                                punchInTime,
                                "-",
                                "-",
                                punchInLoc,
                                "-"
                            )
                        )
                    }else{
                        Toast.makeText(this, "Out of PunchIn Area", Toast.LENGTH_SHORT).show()
                    }
//                }

                buttonEnabled(punchOut)
                buttonDisabled(punchIn)
            }




//             showDatabase("R", date, punchIn, punchInLoc)
        }


        punchOut.setOnClickListener {
            SharedPref.setPunchInEnabled(true)
            getLastLocation()

            if(checkLocationPermission()){
                val calendar = Calendar.getInstance()
                val punchOutTime =
                    SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(calendar.time)
                val punchOutLoc = "${latitude}\n${longitude}"


                userViewModel.updatePunchOutEntry(lastEntry, punchOutTime, punchOutLoc)



                buttonEnabled(punchIn)
                buttonDisabled(punchOut)
            }

        }



    }


    private fun buttonEnabled(btn: Button){
        btn.isEnabled = true
        btn.setBackgroundResource(R.drawable.button_enabled)
    }

    private fun buttonDisabled(btn: Button){
        btn.isEnabled = false
        btn.setBackgroundResource(R.drawable.button_disabled)
    }

    private fun checkLocationPermission():Boolean{
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 2)
            return false
        }


//        if(!isLocationEnabled()){
//            Toast.makeText(this, "Location is offed", Toast.LENGTH_SHORT ).show()
//            startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
//            return false
//        }

        return true
    }

    private fun isLocationEnabled(): Boolean{
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    private fun getLastLocation(){
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        checkLocationPermission()
//        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnCompleteListener{ task ->
                if (task.isSuccessful && task.result != null) {
                    lastLocation = task.result

                    latitude = lastLocation!!.latitude.toString()
                    longitude = lastLocation!!.longitude.toString()
                } else {
                    Toast.makeText(this, "No Location Detected", Toast.LENGTH_SHORT).show()
                }
            }
        }




    }


    fun calculateDistance(
        startLatitude: Double,
        startLongitude: Double,

//        endLatitude: Double= ,
//        endLongitude: Double = 77.0318741
    ): Double {
        val earthRadius = 6371.0 // Earth radius in kilometers
        val endLatitude: Double
        val endLongitude: Double

        if(!switchButton.isChecked){
            endLatitude= 28.4197
            endLongitude = 77.0386
        }
        else{
            endLatitude= 28.4381097
            endLongitude = 77.0318741
        }

        val dLat = Math.toRadians(endLatitude - startLatitude)
        val dLon = Math.toRadians(endLongitude - startLongitude)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(startLatitude)) * cos(Math.toRadians(endLatitude)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }




    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 2) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    }