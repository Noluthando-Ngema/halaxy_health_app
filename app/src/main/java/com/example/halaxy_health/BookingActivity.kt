package com.example.halaxy_health

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BookingActivity : AppCompatActivity() {

    private val db by lazy { AppDatabase.getDatabase(this) }
    private val doctorsList = mutableListOf<Doctor>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        val spnDoctors = findViewById<Spinner>(R.id.spnDoctors)
        val edtDept = findViewById<EditText>(R.id.edtDept)
        val edtDate = findViewById<EditText>(R.id.edtDate)
        val edtTime = findViewById<EditText>(R.id.edtTime)
        val btnBook = findViewById<Button>(R.id.btnBook)

        val today = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        edtDate.setText(today)
        edtTime.setText("10:00 AM")

        lifecycleScope.launch {
            val docs = db.doctorDao().getAllDoctors().filter { it.isApproved || it.status == "APPROVED" }
            doctorsList.clear()
            doctorsList.addAll(docs)

            val names = if (docs.isEmpty()) listOf("Dr. Sarah Smith") else docs.map { if (it.fullName.isNotEmpty()) it.fullName else "Dr. ${it.name} ${it.surname}" }
            val adapter = ArrayAdapter(this@BookingActivity, android.R.layout.simple_spinner_item, names)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnDoctors.adapter = adapter
        }

        btnBook.setOnClickListener {
            val patientEmail = getSharedPreferences("halaxy_session", MODE_PRIVATE).getString("logged_email", "") ?: ""
            val dateStr = edtDate.text.toString().trim()
            val timeStr = edtTime.text.toString().trim()
            val deptStr = edtDept.text.toString().trim().ifEmpty { "General Practice" }

            if (dateStr.isEmpty() || timeStr.isEmpty()) {
                Toast.makeText(this, "Please enter date and time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedIndex = spnDoctors.selectedItemPosition
            val selectedDoc = if (doctorsList.isNotEmpty() && selectedIndex in doctorsList.indices) doctorsList[selectedIndex] else null
            val docEmail = selectedDoc?.email ?: "sarah.smith@halaxy.com"
            val docName = selectedDoc?.fullName?.ifEmpty { "Dr. ${selectedDoc.name} ${selectedDoc.surname}" } ?: "Dr. Sarah Smith"

            lifecycleScope.launch {
                val user = if (patientEmail.isNotEmpty()) db.userDao().getUserByEmail(patientEmail) else null
                val patientName = user?.let { "${it.name} ${it.surname}" } ?: "Patient"

                db.appointmentDao().insertAppointment(
                    Appointment(
                        patientName = patientName,
                        patientEmail = patientEmail,
                        doctorEmail = docEmail,
                        doctorName = docName,
                        time = timeStr,
                        date = dateStr,
                        department = deptStr,
                        status = "Pending"
                    )
                )

                Toast.makeText(this@BookingActivity, "Appointment booked successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
