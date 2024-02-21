package com.example.punchinapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.time.Duration


@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addEntry(user: User)


    @Query("SELECT * FROM user_checkIn WHERE name = :name")
    fun readEntries(name : String) : LiveData<List<User>>


    @Query("DELETE FROM user_checkIn")
    suspend fun deleteEntries()


    @Query("UPDATE user_checkIn SET punchOutTime = :pOutTime, punchOutLoc = :pOutLoc, duration = :duration WHERE id = (SELECT MAX(id) FROM user_checkIn WHERE name = :name )")
    suspend fun updatePunchOutEntry(name: String, pOutTime: String, pOutLoc: String, duration: String)


//    @Query("SELECT MAX(id) FROM user_checkIn")
//    fun getLastId():LiveData<Int>

    @Query("SELECT punchInTime FROM user_checkIn WHERE id = (SELECT MAX(id) FROM user_checkIn WHERE name = :name)")
    suspend fun getPunchInTime(name: String):String


    @Query("SELECT punchOutTime FROM user_checkIn WHERE id = (SELECT MAX(id) FROM user_checkIn WHERE name = :name)")
    suspend fun getPunchOutTime(name: String):String

}