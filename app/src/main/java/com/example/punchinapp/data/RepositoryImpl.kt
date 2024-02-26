package com.example.punchinapp.data

import androidx.lifecycle.LiveData

class RepositoryImpl(private val userDao: UserDao) : RepositoryInterface {
    override suspend fun addEntry(user: User) {
        userDao.addEntry(user)
    }

    override fun readEntries(name: String): LiveData<List<User>> {
        return userDao.readEntries(name)
    }

    override suspend fun deleteEntries() {
        userDao.deleteEntries()
    }

    override suspend fun updatePunchOutEntry(name: String, pOutTime: String, pOutLoc: String, duration: String) {
        userDao.updatePunchOutEntry(name, pOutTime, pOutLoc, duration)
    }

    override suspend fun getPunchInTime(name: String): String {
        return userDao.getPunchInTime(name)
    }

    override suspend fun getPunchOutTime(name: String): String {
        return userDao.getPunchOutTime(name)
    }


}