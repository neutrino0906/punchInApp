package com.example.punchinapp

import android.app.Application

class PunchInApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        SharedPref.init(this)
    }
}