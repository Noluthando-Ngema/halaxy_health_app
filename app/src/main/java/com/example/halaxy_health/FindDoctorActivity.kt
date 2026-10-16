package com.example.halaxy_health

import android.content.Intent
import android.os.Bundle
import android.widget.*
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FindDoctorActivity : AppCompatActivity() {
    private lateinit var allDoctors: List<Doctor>
    private lateinit var adapter: FindDoctorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_doctor)

        val db = AppDatabase.getDatabase(this)
        val rec = findViewById<RecyclerView>(R.id.recDoctors)
        rec.layoutManager = GridLayoutManager(this, 2) // 2 per row on phone, 3 on tablet
        val edtSearch = findViewById<EditText>(R.id.edtSearch)
        val edtLocation = findViewById<EditText>(R.id.edtLocation)

        // default demo doctors - Sarah Jenkins and real signed up doctors
        lifecycleScope.launch {
            db.doctorDao().getApprovedDoctors().collect { doctors ->

                // Merge with default demo data from screenshot if not in DB yet
                val demo = listOf(
                    Doctor(fullName="Sarah Jenkins", ahpraNumber="MED001", specialty="GENERAL PRACTITIONER", clinicName="Central Clinic", email="sarah.jenkins@halaxy.com", passwordHash="", isApproved=true, status="APPROVED"),
                    Doctor(fullName="Michael Chen", ahpraNumber="MED002", specialty="PHYSIOTHERAPIST", clinicName="Physio Plus", email="michael.chen@halaxy.com", passwordHash="", isApproved=true, status="APPROVED"),
                    Doctor(fullName="Elena Rodriguez", ahpraNumber="MED003", specialty="CLINICAL PSYCHOLOGIST", clinicName="Mind Care", email="elena.rodriguez@halaxy.com", passwordHash="", isApproved=true, status="APPROVED")
                )
                val combined = (doctors + demo).distinctBy { it.email }

                allDoctors = combined
                adapter = FindDoctorAdapter(combined) { doctor ->
                    // save recently viewed
                    getSharedPreferences("recent_prefs", MODE_PRIVATE).edit().putString("recent_1", "Dr. ${doctor.fullName} (${doctor.specialty.take(2)})").apply()
                    // go to book appointment activity
                    val intent = Intent(this@FindDoctorActivity, BookAppointmentActivity::class.java)
                    intent.putExtra("doctorEmail", doctor.email)
                    intent.putExtra("doctorName", "Dr. ${doctor.fullName}")
                    intent.putExtra("specialty", doctor.specialty)
                    startActivity(intent)
                }
                rec.adapter = adapter

                // Recently Viewed
                val recentPrefs = getSharedPreferences("recent_prefs", MODE_PRIVATE)
                findViewById<TextView>(R.id.txtRecent1).text = "• ${recentPrefs.getString("recent_1", "Dr. Sarah Jenkins (GP)")}"
            }
        }

        findViewById<Button>(R.id.btnSearch).setOnClickListener {
            val query = edtSearch.text.toString().lowercase()
            val filtered = allDoctors.filter {
                it.fullName.lowercase().contains(query) ||
                        it.specialty.lowercase().contains(query) ||
                        it.clinicName.lowercase().contains(query)
            }
            adapter.updateList(filtered)
        }
    }
}