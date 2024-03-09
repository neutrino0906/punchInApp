package com.example.punchinapp.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application){

    private val repository: RepositoryImpl

    init {
        val userDao = UserDatabase.getDatabase(application).userDao()
        repository = RepositoryImpl(userDao)
    }

    fun addEntry(user: User){
        viewModelScope.launch(Dispatchers.IO){
            repository.addEntry(user)
        }
    }

    fun deleteEntries(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteEntries()
        }
    }

    fun readEntries(name: String): LiveData<List<User>>{
        return repository.readEntries(name)
    }

    fun updatePunchOutEntry(name: String, pOutTime: String, pOutLoc: String, duration: Int){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updatePunchOutEntry(name, pOutTime, pOutLoc, duration)
        }
    }

    suspend fun getPunchInTime(name: String):String{
        return repository.getPunchInTime(name)
    }

    suspend fun getPunchOutTime(name : String):String{
        return repository.getPunchOutTime(name)
    }


    suspend fun getTotalDuration(name: String, date: String):Int{
        return repository.getTotalDuration(name, date)
    }

}