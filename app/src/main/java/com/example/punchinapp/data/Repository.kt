package com.example.punchinapp.data

import androidx.lifecycle.LiveData

interface RepositoryInterface {

    suspend fun addEntry(user: User)

    fun readEntries(name: String): LiveData<List<User>>

    suspend fun deleteEntries()

    suspend fun updatePunchOutEntry(name: String, pOutTime: String, pOutLoc: String, duration: Int)

    suspend fun getPunchInTime(name: String): String

    suspend fun getPunchOutTime(name: String): String

    suspend fun getTotalDuration( name: String, date: String): Int

}