package com.example.halaxy_health

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DoctorDashboardActivity : AppCompatActivity() {
    private var currentFilter = "All"
    private val db by lazy { AppDatabase.getDatabase(this) }
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_dashboard)

        email = getSharedPreferences("halaxy_session", MODE_PRIVATE).getString("logged_doctor", "sarah.smith@halaxy.com")!!
        val today = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())

        val tabs = mapOf(
            findViewById<Button>(R.id.tabAll) to "All",
            findViewById<Button>(R.id.tabPending) to "Pending",
            findViewById<Button>(R.id.tabApproved) to "Approved",
            findViewById<Button>(R.id.tabCompleted) to "Completed",
            findViewById<Button>(R.id.tabCancelled) to "Cancelled"
        )

        fun selectTab(selected: String) {
            currentFilter = selected
            tabs.forEach { (btn, filter) ->
                btn.setBackgroundResource(if (filter == selected) R.drawable.bg_tab_selected else R.drawable.bg_tab_unselected)
            }
            loadAppointments()
        }

        tabs.forEach { (btn, filter) -> btn.setOnClickListener { selectTab(filter) } }

        selectTab("All")
        observeStats(today)
    }

    private fun observeStats(today: String) {
        lifecycleScope.launch {
            db.appointmentDao().getTodaysCount(email, today).collect {
                findViewById<TextView>(R.id.txtTodayCount).text = it.toString()
            }
        }
        lifecycleScope.launch {
            db.appointmentDao().getPendingCount(email).collect {
                findViewById<TextView>(R.id.txtPendingCount).text = it.toString()
            }
        }
    }

    private fun loadAppointments() {
        val recycler = findViewById<RecyclerView>(R.id.recAppointments)
        recycler.layoutManager = LinearLayoutManager(this)
        lifecycleScope.launch {
            db.appointmentDao().getFilteredForDoctor(email, currentFilter).collect { list ->
                recycler.adapter = TableAppointmentAdapter(
                    list,
                    onApprove = { id -> updateStatus(id, "Approved") },
                    onCancel = { id -> updateStatus(id, "Cancelled") },
                    onComplete = { id ->
                        lifecycleScope.launch {
                            db.appointmentDao().updateStatus(id, "Completed")
                            Toast.makeText(this@DoctorDashboardActivity, "Marked completed - patient sees it too", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }

    private fun updateStatus(id: Int, status: String) {
        lifecycleScope.launch {
            db.appointmentDao().updateStatus(id, status)
        }
    }
}
