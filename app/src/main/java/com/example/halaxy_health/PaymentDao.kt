package com.example.halaxy_health

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Insert
    suspend fun insertPayment(payment: Payment)

    @Query("SELECT * FROM payments WHERE patientEmail = :email")
    fun getPaymentsForPatient(email: String): Flow<List<Payment>>
}
