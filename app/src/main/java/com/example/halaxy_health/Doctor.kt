package com.example.halaxy_health

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "doctors")
data class Doctor(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val speciality: String = "General Physician", // FIXED
    val passwordHash: String = "", // FIXED
    val isApproved: Boolean = false,
    val status: String = "PENDING"
)