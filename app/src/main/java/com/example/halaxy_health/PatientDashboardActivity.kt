package com.example.halaxy_health

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class PatientDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_dashboard)

        val txtHello = findViewById<TextView>(R.id.txtHello)
        val prefs = getSharedPreferences("halaxy_session", MODE_PRIVATE)
        val email = prefs.getString("logged_email", null)
        if (email == null) {
            // you are not logged in as patient, go to patient login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        if (email != null) {
            val db = AppDatabase.getDatabase(this)
            lifecycleScope.launch {
                val user = db.userDao().getUserByEmail(email)
                if (user != null) {
                    txtHello.text = "Hello, ${user.name}"
                }
            }
        }

        findViewById<Button>(R.id.btnScheduleNow).setOnClickListener {
            startActivity(Intent(this, FindDoctorActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.cardMyAppointments).setOnClickListener {
            startActivity(Intent(this, MyAppointmentsActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.cardMakeBooking2).setOnClickListener {
            startActivity(Intent(this, BookAppointmentActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.cardEditProfile)?.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        findViewById<View>(R.id.cardLearningCentre).setOnClickListener {
            // open learning centre
            startActivity(Intent(this, LearningCentreActivity::class.java))
        }

        // apply dark/light mode
        ThemeHelper.applyTheme(this)
    }
}
