package com.example.punchinapp

import android.content.Context
import android.content.SharedPreferences

object SharedPref {

    const val IS_USER_LOGGED_IN = "isUserLoggedIn"

    private const val NAME = "AuthenticationPref"
    private const val MODE = Context.MODE_PRIVATE

    private lateinit var preferences : SharedPreferences

    fun init(context: Context){
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    fun setLoggedIn(key: String, value: Boolean){
        preferences.edit().putBoolean(key, value).apply()
    }

    fun isLoggedIn(key: String): Boolean{
        return preferences.getBoolean(key, false)
    }

}