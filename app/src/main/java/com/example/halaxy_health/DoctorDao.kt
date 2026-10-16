package com.example.halaxy_health

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DoctorDao {
    @Insert
    suspend fun insertDoctor(doctor: Doctor)

    @Query("SELECT * FROM doctors WHERE email = :email LIMIT 1")
    suspend fun getDoctorByEmail(email: String): Doctor?

    @Query("SELECT * FROM doctors WHERE status = 'PENDING'")
    suspend fun getPendingDoctors(): List<Doctor>
    @Query("SELECT * FROM doctors WHERE isApproved = 1")
    fun getApprovedDoctors(): Flow<List<Doctor>>

    @Query("SELECT * FROM doctors")
    suspend fun getAllDoctors(): List<Doctor>

    @Query("UPDATE doctors SET isApproved = :approved, status = :status WHERE id = :id")
    suspend fun updateApproval(id: Int, approved: Boolean, status: String)

    @Update
    suspend fun updateDoctor(doctor: Doctor)

    @Query("DELETE FROM doctors WHERE id = :id")
    suspend fun deleteDoctor(id: Int)
}
