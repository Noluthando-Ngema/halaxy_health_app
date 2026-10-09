package com.example.halaxy_health

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Database(entities = [User::class, Doctor::class, Appointment::class, Payment::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun doctorDao(): DoctorDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun paymentDao(): PaymentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "halaxy_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val database = getDatabase(context)
                                val dao = database.doctorDao()
                                val hasher = PasswordHasher
                                dao.insertDoctor(
                                    Doctor(
                                        name = "Sarah",
                                        surname = "Smith",
                                        fullName = "Dr. Sarah Smith",
                                        email = "sarah.smith@halaxy.com",
                                        passwordHash = hasher.hashPassword("Doctor123!"),
                                        specialization = "General Physician",
                                        isApproved = true,
                                        status = "APPROVED"
                                    )
                                )
                                dao.insertDoctor(
                                    Doctor(
                                        name = "James",
                                        surname = "Molefe",
                                        fullName = "Dr. James Molefe",
                                        email = "james.molefe@halaxy.com",
                                        passwordHash = hasher.hashPassword("Doctor123!"),
                                        specialization = "Cardiology",
                                        isApproved = true,
                                        status = "APPROVED"
                                    )
                                )
                                val appDao = database.appointmentDao()
                                val today = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
                                appDao.insertAppointment(
                                    Appointment(
                                        patientName = "John Doe",
                                        patientEmail = "john@gmail.com",
                                        doctorEmail = "sarah.smith@halaxy.com",
                                        doctorName = "Dr. Sarah Smith",
                                        time = "10:00 AM",
                                        date = today,
                                        department = "General Practice",
                                        status = "Pending"
                                    )
                                )
                                appDao.insertAppointment(
                                    Appointment(
                                        patientName = "Jane Smith",
                                        patientEmail = "jane@gmail.com",
                                        doctorEmail = "sarah.smith@halaxy.com",
                                        doctorName = "Dr. Sarah Smith",
                                        time = "11:30 AM",
                                        date = today,
                                        department = "General Practice",
                                        status = "Approved"
                                    )
                                )
                                appDao.insertAppointment(
                                    Appointment(
                                        patientName = "Mark Johnson",
                                        patientEmail = "mark@gmail.com",
                                        doctorEmail = "sarah.smith@halaxy.com",
                                        doctorName = "Dr. Sarah Smith",
                                        time = "02:00 PM",
                                        date = today,
                                        department = "General Practice",
                                        status = "Pending"
                                    )
                                )
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
