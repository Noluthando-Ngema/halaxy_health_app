package com.example.halaxy_health

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val surname: String,
    val email: String,
    val passwordHash: String,
    val isApproved: Boolean = false, // NEW - admin must approve
    val status: String = "PENDING" // PENDING, APPROVED, DECLINED
)