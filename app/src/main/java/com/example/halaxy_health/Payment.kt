package com.example.halaxy_health

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payments")
data class Payment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientEmail: String,
    val amount: Double,
    val date: String
)