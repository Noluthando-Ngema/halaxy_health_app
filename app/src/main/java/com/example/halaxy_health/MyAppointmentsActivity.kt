package com.example.halaxy_health

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MyAppointmentsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_appointments)

        val prefs = getSharedPreferences("halaxy_session", MODE_PRIVATE)
        val patientEmail = prefs.getString("logged_email", "")!!
        val db = AppDatabase.getDatabase(this)

        val recycler = findViewById<RecyclerView>(R.id.recAppointments)
        val emptyLayout = findViewById<LinearLayout>(R.id.layoutEmpty)
        recycler.layoutManager = LinearLayoutManager(this)

        // observes same table doctor writes to
        lifecycleScope.launch {
            db.appointmentDao().getForPatient(patientEmail).collect { list ->
                if (list.isEmpty()) {
                    emptyLayout.visibility = View.VISIBLE
                    recycler.visibility = View.GONE
                } else {
                    emptyLayout.visibility = View.GONE
                    recycler.visibility = View.VISIBLE
                    recycler.adapter = PatientAppointmentAdapter(list,
                        onCancel = { id ->
                            lifecycleScope.launch {
                                db.appointmentDao().updateStatus(id, "Cancelled")
                                Toast.makeText(this@MyAppointmentsActivity, "Appointment cancelled", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onView = { appt ->
                            // show details dialog
                            AlertDialog.Builder(this@MyAppointmentsActivity)
                                .setTitle("Appointment Details")
                                .setMessage("Doctor: ${appt.doctorName}\nDept: ${appt.department}\nDate: ${appt.dateTime}\nStatus: ${appt.status}\nAmount: R${appt.amount}")
                                .setPositiveButton("OK", null)
                                .show()
                        }
                    )
                }
            }
        }

        findViewById<Button>(R.id.btnBookFirst).setOnClickListener {
            startActivity(Intent(this, PatientDashboardActivity::class.java))
        }
    }
}
