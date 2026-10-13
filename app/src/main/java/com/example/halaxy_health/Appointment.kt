package com.example.halaxy_health

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appointments")
data class Appointment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientName: String,
    val patientEmail: String,
    val doctorEmail: String,
    val doctorName: String,
    val time: String, // 10:00 AM
    val date: String, // 25 May 2026
    val department: String, // General Practice
    var status: String = "Pending", // Pending, Approved, Completed
    val amount: Double = 500.0, // Default to R500
    val reason: String = "General Consultation"
) {
    val dateTime: String
        get() = "$date $time"
}