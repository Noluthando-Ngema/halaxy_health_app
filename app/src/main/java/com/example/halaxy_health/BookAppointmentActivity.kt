package com.example.halaxy_health

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BookAppointmentActivity : AppCompatActivity() {

    // selected values
    private var selectedDateStr = ""
    private var selectedDisplayDate = ""
    private var selectedTime = ""
    private var selectedDoctorEmail = ""
    private var selectedDoctorName = "Dr. Sarah Jenkins"

    // calendar tracking
    private var currentCalendar = Calendar.getInstance()
    private var selectedDay = 12

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_appointment)

        // get doctor from previous screen
        selectedDoctorEmail = intent.getStringExtra("doctorEmail") ?: ""
        selectedDoctorName = intent.getStringExtra("doctorName") ?: "Dr. Sarah Jenkins"
        val specialty = intent.getStringExtra("specialty") ?: "General Practitioner"

        // dynamic subtitle
        findViewById<TextView>(R.id.txtSubtitle).text =
            "Select a preferred date and time to consult with $selectedDoctorName."

        // database and session
        val db = AppDatabase.getDatabase(this)
        val prefs = getSharedPreferences("halaxy_session", MODE_PRIVATE)
        val patientEmail = prefs.getString("logged_email", "") ?: ""
        val patientName = prefs.getString("logged_name", "Patient") ?: "Patient"

        // init calendar
        setupCalendar()

        // load available times
        loadTimesForDate(db)

        // previous month
        findViewById<Button>(R.id.btnPrev).setOnClickListener {
            // go to previous month
            currentCalendar.add(Calendar.MONTH, -1)
            setupCalendar()
        }

        // next month
        findViewById<Button>(R.id.btnNext).setOnClickListener {
            // go to next month
            currentCalendar.add(Calendar.MONTH, 1)
            setupCalendar()
        }

        // confirm booking
        findViewById<Button>(R.id.btnConfirm).setOnClickListener {
            // read reason
            val reason = findViewById<EditText>(R.id.edtReason).text.toString().ifEmpty {
                "General Consultation"
            }

            // check time selected
            if (selectedTime.isEmpty()) {
                Toast.makeText(this, "Select a time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                // create appointment object
                val appointment = Appointment(
                    patientName = patientName,
                    patientEmail = patientEmail,
                    doctorEmail = selectedDoctorEmail,
                    doctorName = selectedDoctorName,
                    time = selectedTime,
                    date = selectedDateStr,
                    department = specialty,
                    status = "Pending",
                    amount = 500.0,
                    reason = reason
                )

                // insert and get id
                val id = db.appointmentDao().insertAndGetId(appointment)

                runOnUiThread {
                    // go to payment screen
                    val intent = Intent(this@BookAppointmentActivity, PaymentActivity::class.java)
                    intent.putExtra("amount", 500.0)
                    intent.putExtra("appointmentId", id.toInt())
                    intent.putExtra("doctorName", selectedDoctorName)
                    intent.putExtra("dateTime", "$selectedDisplayDate $selectedTime")
                    startActivity(intent)
                }
            }
        }
    }

    private fun setupCalendar() {
        // show month name
        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        findViewById<TextView>(R.id.txtMonth).text = monthFormat.format(currentCalendar.time)

        // generate days 9-15 like screenshot
        val days = (9..15).toList()

        val rec = findViewById<RecyclerView>(R.id.recCalendar)
        rec.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rec.adapter = CalendarAdapter(days, selectedDay) { day ->

            // when day clicked
            selectedDay = day
            val dateCal = currentCalendar.clone() as Calendar
            dateCal.set(Calendar.DAY_OF_MONTH, day)

            // format for db and display
            selectedDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dateCal.time)
            selectedDisplayDate = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(dateCal.time)

            findViewById<TextView>(R.id.txtSelectedDate).text = selectedDisplayDate

            // reload times
            loadTimesForDate(AppDatabase.getDatabase(this))
        }

        // set default selected date
        val defCal = currentCalendar.clone() as Calendar
        defCal.set(Calendar.DAY_OF_MONTH, selectedDay)
        selectedDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(defCal.time)
        selectedDisplayDate = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(defCal.time)
        findViewById<TextView>(R.id.txtSelectedDate).text = selectedDisplayDate
    }

    private fun loadTimesForDate(db: AppDatabase) {
        // all possible slots
        val slots = listOf(
            "09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM",
            "11:00 AM", "11:30 AM", "02:00 PM", "02:30 PM",
            "03:00 PM", "03:30 PM"
        )

        val recTimes = findViewById<RecyclerView>(R.id.recTimes)
        recTimes.layoutManager = GridLayoutManager(this, 2)

        lifecycleScope.launch {
            // get already booked times
            val bookedTimes = db.appointmentDao()
                .getBookedTimesForDoctorOnDate(selectedDoctorEmail, selectedDateStr)

            runOnUiThread {
                // show adapter with booked check
                recTimes.adapter = TimeSlotAdapter(slots, bookedTimes) { time ->
                    selectedTime = time
                }
            }
        }
    }
}