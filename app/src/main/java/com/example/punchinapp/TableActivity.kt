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
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.provider.Settings
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.text.NumberFormat
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
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var signOutBtn: Button
    private var auth : FirebaseUser? = null
    private lateinit var scrollview: ScrollView
    private lateinit var switchButton : MaterialSwitch
    private lateinit var totalLogin : TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table)


        // Status Bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.buttonEnabled)


        //Initializations
        punchIn = findViewById(R.id.punchInButton)
        punchOut = findViewById(R.id.punchOutButton)
        table = findViewById(R.id.tableLayout)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        auth = FirebaseAuth.getInstance().currentUser
        signOutBtn = findViewById(R.id.SignOutButton)
        scrollview = findViewById(R.id.table_scrollview)
        switchButton = findViewById(R.id.switch_btn)
        totalLogin = findViewById(R.id.totalLoginTime)
        var totalLoginTime = 0L

        //Google SignIn
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()


        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Getting Last Recorded button status of the database
        lifecycleScope.launch(Dispatchers.IO){
            val isPunchInEnabled = userViewModel.getPunchOutTime(auth?.displayName.toString())
            try{
                if (isPunchInEnabled != "-") {
                    withContext(Dispatchers.Main){
                        buttonEnabled(punchIn)
                        buttonDisabled(punchOut)
                    }


                } else {
                    withContext(Dispatchers.Main){
                        buttonEnabled(punchOut)
                        buttonDisabled(punchIn)
                    }

                }
            }catch (e: Exception){
                buttonEnabled(punchIn)
                buttonDisabled(punchOut)
            }
        }

        latitude = "0"
        longitude = "0"

        //getting current location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getLastLocation()

        CoroutineScope(Dispatchers.IO).launch{
            try{
                val calendar = Calendar.getInstance()
                val temp = userViewModel.getTotalDuration(auth?.displayName.toString(), SimpleDateFormat.getDateInstance().format(calendar.time))



                withContext(Dispatchers.Main) {
                    if (temp != 0) {
                        totalLoginTime = temp.toLong()
                        totalLogin.text = "Total Login Time on ${
                            SimpleDateFormat.getDateInstance().format(calendar.time)
                        }:  ${longTimeToString(temp.toLong())}"
                    } else {
                        totalLogin.text = "Total Login Time on ${
                            SimpleDateFormat.getDateInstance().format(calendar.time)
                        }:  00:00:00"
                    }

                    if(SharedPref.getLastDate() != SimpleDateFormat.getDateInstance().format(calendar.time)){
                        totalLogin.text = "Total Login Time on ${
                            SimpleDateFormat.getDateInstance().format(calendar.time)
                        }:  00:00:00"
                    }
                }
            }
            catch (e:Exception){
                val calendar = Calendar.getInstance()
                totalLogin.text = "Total Login Time on ${
                    SimpleDateFormat.getDateInstance().format(calendar.time)
                }:  00:00:00"
            }
        }

        //Reading the entries in the database
        userViewModel.readEntries(auth?.displayName.toString()).observe(this){
            while (table.childCount > 1) {
                table.removeView(table.getChildAt(table.childCount - 1))
            }

            for(i in it){

                val tr = TableRow(this)
                val tv0 = TextView(this)
                tv0.text = i.name
                tv0.setPadding(10,10,10,10)
                tr.addView(tv0)

                val tv1 = TextView(this)
                tv1.text = i.date
                tv1.setPadding(10,10,10,10)
                tr.addView(tv1)

                val tv2 = TextView(this)
                tv2.text = i.punchInTime
                tv2.setPadding(10,10,10,10)
                tr.addView(tv2)

                val tv3 = TextView(this)
                tv3.text = i.punchInLoc
                tv3.setPadding(10,10,10,10)
                tr.addView(tv3)

                val tv4 = TextView(this)
                tv4.text = i.punchOutTime
                tv4.setPadding(10,10,10,10)
                tr.addView(tv4)

                val tv5 = TextView(this)
                tv5.text = i.punchOutLoc
                tv5.setPadding(10,10,10,10)
                tr.addView(tv5)

                if(i.duration.toString()!="-") {

                    var duration = Integer.valueOf(i.duration.toString())

                    val seconds = duration % 60
                    duration /= 60

                    val minutes = duration
                    duration /= 60

                    val hours = duration

                    val tv6 = TextView(this)
                    tv6.text = "${hours} hours ${minutes} minutes ${seconds} seconds"
                    tv6.setPadding(10, 10, 10, 10)
                    tr.addView(tv6)
                }
                else{
                    val tv6 = TextView(this)
                    tv6.text = "-"
                    tv6.setPadding(10, 10, 10, 10)
                    tr.addView(tv6)
                }

                tr.setPadding(10,10,10,10)
                table.addView(tr)
            }
        }

        // Scroll the list to the end
        scrollview.post{
            scrollview.fullScroll(ScrollView.FOCUS_DOWN)
        }


        // Sign Out button (Revokes the google access)
        signOutBtn.setOnClickListener {
            SharedPref.setLoggedIn(SharedPref.IS_USER_LOGGED_IN,false)
            googleSignInClient.revokeAccess()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }


        // Punch In button
        punchIn.setOnClickListener{


            checkLocationPermission()
            getLastLocation()



            if (checkLocationPermission()) {
                getLastLocation()
                    if (calculateDistance(latitude.toDouble(), longitude.toDouble()) < 0.05) {

                        val calendar = Calendar.getInstance()
                        SharedPref.setLastDate(SimpleDateFormat.getDateInstance().format(calendar.time))
                        val punchInTime = SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(calendar.time)



                        val date = SimpleDateFormat.getDateInstance().format(calendar.time)
                        val punchInLoc = "${latitude}\n${longitude}"



                        userViewModel.addEntry(
                            User(
                                0,
                                auth?.displayName.toString(),
                                date,
                                punchInTime,
                                "-",
                                0,
                                punchInLoc,
                                "-",
                            )
                        )
                        buttonEnabled(punchOut)
                        buttonDisabled(punchIn)
                    } else{
                        Toast.makeText(this, "Out of PunchIn Area", Toast.LENGTH_SHORT).show()
                    }


            }
            else{
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            }

            scrollview.post{
                scrollview.fullScroll(ScrollView.FOCUS_DOWN)
            }

        }

        // Punch out Button
        punchOut.setOnClickListener {


            getLastLocation()


            if(checkLocationPermission()){


                lifecycleScope.launch(Dispatchers.IO) {
                    val calendar = Calendar.getInstance()
                    SharedPref.setLastDate(SimpleDateFormat.getDateInstance().format(calendar.time))
                    val punchOutTime =
                        SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(calendar.time)

                    val punchOutLoc = "${latitude}\n${longitude}"

                    val punchInTime: String = userViewModel.getPunchInTime(auth?.displayName.toString())
                    val time1 = try{ SimpleDateFormat("hh:mm:ss").parse(punchInTime)
                    }catch (e: Exception){
                        SimpleDateFormat("hh:mm:ss").parse("00:00:00")
                    }
                    val time2 = SimpleDateFormat("hh:mm:ss").parse(punchOutTime)


                    val diff = time2!!.time - time1!!.time


                    totalLoginTime += (diff/1000)

                    withContext(Dispatchers.Main){ totalLogin.text = "Total Login Time on ${SimpleDateFormat.getDateInstance().format(calendar.time)}:  ${longTimeToString(totalLoginTime)}" }


//                    if(diff>28800000) {
                        userViewModel.updatePunchOutEntry(
                            auth?.displayName.toString(),
                            punchOutTime,
                            punchOutLoc,
                            ((diff/1000).toInt())
                        )

                        withContext(Dispatchers.Main){
                            buttonEnabled(punchIn)
                            buttonDisabled(punchOut)
                        }
//                    }
//                    else {
//                        withContext(Dispatchers.Main){
//                            Toast.makeText(baseContext, "Please complete 8 hrs", Toast.LENGTH_SHORT)
//                                .show()
//                        }
//                    }
                }

            }

            scrollview.post{
                scrollview.fullScroll(ScrollView.FOCUS_DOWN)
            }

        }
    }

    //Converting seconds into 12 hour time format time format
    private fun longTimeToString(a: Long): String{
        var duration = a
        val seconds = duration % 60
        duration /= 60

        val minutes = duration % 60
        duration /= 60

        val hours = duration

        val f: NumberFormat = DecimalFormat("00")

        return "${f.format(hours)} : ${f.format(minutes)} : ${f.format(seconds)}"
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



        if(!isLocationEnabled()){
            Toast.makeText(this, "Location is disabled", Toast.LENGTH_SHORT ).show()
            startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            return false
        }

        return true
    }

    private fun isLocationEnabled(): Boolean{
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    private fun getLastLocation(){
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


    private fun calculateDistance(
        startLatitude: Double,
        startLongitude: Double,
    ): Double {
        val earthRadius = 6371.0 // Earth radius in kilometers
        val endLatitude: Double
        val endLongitude: Double

        if(!switchButton.isChecked){
//            endLatitude= 28.4197
//            endLongitude = 77.0386
              endLatitude = 28.7546236
              endLongitude = 77.4942542
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
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    }