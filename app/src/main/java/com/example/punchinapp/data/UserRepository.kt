package com.example.punchinapp.data

import androidx.lifecycle.LiveData

class UserRepository(private val userDao: UserDao) {


    fun readEntries(name: String): LiveData<List<User>>{
        return userDao.readEntries(name)
    }

    suspend fun addEntry(user:User){
        userDao.addEntry(user)
    }
    //

    suspend fun deleteEnteries(){
        userDao.deleteEntries()
    }

    suspend fun updatePunchOutEntry(name: String, pOutTime: String, pOutLoc: String, duration: String){
        userDao.updatePunchOutEntry(name, pOutTime, pOutLoc, duration)
    }

//    fun getLastId():LiveData<Int>{
//        return userDao.getLastId()
//    }

    suspend fun getPunchInTime(name: String):String{
        return userDao.getPunchInTime(name)
    }

    suspend fun getPunchOutTime(name: String):String{
        return userDao.getPunchOutTime(name)
    }

}