package com.example.punchinapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time
import java.util.Date

@Entity(tableName = "user_checkIn")
class User (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val date: String,
    val punchInTime : String,
    val punchOutTime : String,
    val duration : Int,
    val punchInLoc : String,
    val punchOutLoc : String,
)