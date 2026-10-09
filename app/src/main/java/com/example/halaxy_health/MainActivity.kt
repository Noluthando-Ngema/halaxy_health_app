package com.example.halaxy_health

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("halaxy_session", MODE_PRIVATE)
        val loggedEmail = prefs.getString("logged_email", null)
        val loggedDoctor = prefs.getString("logged_doctor", null)
        val loggedAdmin = prefs.getString("logged_admin", null)

        when {
            loggedAdmin != null -> {
                startActivity(Intent(this, AdminDashboardActivity::class.java))
            }
            loggedDoctor != null -> {
                startActivity(Intent(this, DoctorDashboardActivity::class.java))
            }
            loggedEmail != null -> {
                startActivity(Intent(this, PatientDashboardActivity::class.java))
            }
            else -> {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
        finish()
    }
}
