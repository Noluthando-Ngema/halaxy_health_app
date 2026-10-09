package com.example.halaxy_health

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.halaxy_health.Appointment
import kotlinx.coroutines.flow.Flow


    @Dao
    interface AppointmentDao {
        @Insert suspend fun insertAppointment(a: Appointment)
        @Query("SELECT * FROM appointments WHERE doctorEmail = :email AND date = :today")
        fun getTodaysAppointmentsForDoctor(email: String, today: String): Flow<List<Appointment>>

        @Query("SELECT * FROM appointments WHERE doctorEmail = :email")
        fun getAllForDoctor(email: String): Flow<List<Appointment>>

        @Query("SELECT COUNT(*) FROM appointments WHERE doctorEmail = :email AND date = :today")
        fun getTodaysCount(email: String, today: String): Flow<Int>

        @Query("SELECT COUNT(*) FROM appointments WHERE doctorEmail = :email AND status = 'Pending'")
        fun getPendingCount(email: String): Flow<Int>

        @Query("SELECT COUNT(*) FROM appointments WHERE doctorEmail = :email AND status = 'Completed'")
        fun getCompletedCount(email: String): Flow<Int>

        @Query("SELECT SUM(amount) FROM appointments WHERE doctorEmail = :email AND date = :today AND status = 'Completed'")
        fun getTodaysEarnings(email: String, today: String): Flow<Double?>

        @Query("UPDATE appointments SET status = 'Completed' WHERE id = :id")
        suspend fun markCompleted(id: Int)

        @Query("SELECT * FROM appointments WHERE patientEmail = :patientEmail")
        fun getForPatient(patientEmail: String): Flow<List<Appointment>>

        @Query("UPDATE appointments SET status = :status WHERE id = :id")
        suspend fun updateStatus(id: Int, status: String)

        @Query("SELECT * FROM appointments WHERE doctorEmail = :email AND (:filter = 'All' OR status = :filter) ORDER BY dateTime ASC")
        fun getFilteredForDoctor(email: String, filter: String): Flow<List<Appointment>>
    }