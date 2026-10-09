package com.example.halaxy_health

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "doctors")
data class Doctor(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "",
    val surname: String = "",
    val fullName: String = "",
    val ahpraNumber: String = "",
    val specialty: String = "",
    val clinicName: String = "",
    val email: String,
    val passwordHash: String,
    val specialization: String = "General",
    val isApproved: Boolean = false,
    val status: String = "PENDING"
)
