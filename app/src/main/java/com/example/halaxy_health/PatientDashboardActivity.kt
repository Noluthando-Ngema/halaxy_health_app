package com.example.halaxy_health

import android.content.Intent
import android.os.Bundle
import android.view.View
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

        if (email != null) {
            val db = AppDatabase.getDatabase(this)
            lifecycleScope.launch {
                val user = db.userDao().getUserByEmail(email)
                if (user != null) {
                    txtHello.text = "Hello, ${user.name}"
                }
            }
        }

        findViewById<View>(R.id.btnScheduleNow).setOnClickListener {
            startActivity(Intent(this, BookingActivity::class.java))
        }
        findViewById<View>(R.id.cardAppointments).setOnClickListener {
            startActivity(Intent(this, MyAppointmentsActivity::class.java))
        }
        findViewById<View>(R.id.cardProgress).setOnClickListener {
            Toast.makeText(this, "Track Progress coming soon", Toast.LENGTH_SHORT).show()
        }
        findViewById<View>(R.id.cardPayment).setOnClickListener {
            Toast.makeText(this, "Payment History coming soon", Toast.LENGTH_SHORT).show()
        }
        findViewById<View>(R.id.cardEditProfile).setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }
    }
}
