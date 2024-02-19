package com.example.punchinapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class SplashScreenActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        val loggedIn = SharedPref.isLoggedIn(SharedPref.IS_USER_LOGGED_IN)

        if(loggedIn){
            val intent = Intent(this, TableActivity::class.java)
            startActivity(intent)
        }
        else{
            startActivity(Intent(this, LoginActivity::class.java))
        }

        finish()
    }
}