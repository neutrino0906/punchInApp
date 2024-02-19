package com.example.punchinapp.data

import androidx.lifecycle.LiveData

class UserRepository(private val userDao: UserDao) {

//    val readAllData: LiveData<User> = userDao.readAll()

    fun readEntries(name: String): LiveData<List<User>>{
        return userDao.readEntries(name)
    }

    suspend fun addEntry(user:User){
        userDao.addEntry(user)
    }

    suspend fun deleteEnteries(){
        userDao.deleteEntries()
    }

    suspend fun updatePunchOutEntry(id:Int, pOutTime: String, pOutLoc: String){
        userDao.updatePunchOutEntry(id,pOutTime, pOutLoc)
    }

    fun getLastId():LiveData<Int>{
        return userDao.getLastId()
    }

}