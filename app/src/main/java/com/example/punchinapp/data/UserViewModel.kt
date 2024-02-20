package com.example.punchinapp.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application){

//    val readAllData: LiveData<User>
    private val repository: UserRepository

    init {
        val userDao = UserDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
    }

    fun addEntry(user: User){
        viewModelScope.launch(Dispatchers.IO){
            repository.addEntry(user)
        }
    }

    fun deleteEntries(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteEnteries()
        }
    }

    fun readEntries(name: String): LiveData<List<User>>{
        return repository.readEntries(name)
    }

    fun updatePunchOutEntry(id:Int, pOutTime: String, pOutLoc: String, duration: String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updatePunchOutEntry(id,pOutTime, pOutLoc, duration)
        }
    }

    fun getLastId(): LiveData<Int>{
        return repository.getLastId()
    }

    suspend fun getPunchInTime(id:Int):String{
        return repository.getPunchInTime(id)
    }

}