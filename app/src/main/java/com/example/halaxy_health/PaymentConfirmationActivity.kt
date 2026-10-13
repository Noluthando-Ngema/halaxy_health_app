package com.example.halaxy_health

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PaymentConfirmationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_confirmation)

        // get details
        val doctorName = intent.getStringExtra("doctorName") ?: ""
        val dateTime = intent.getStringExtra("dateTime") ?: ""
        val amount = intent.getDoubleExtra("amount", 500.0)

        // show confirmation text
        findViewById<TextView>(R.id.txtConfirmDetails).text =
            "$doctorName\n$dateTime\nPaid: R$amount"

        findViewById<Button>(R.id.btnDone).setOnClickListener {
            // go back to dashboard
            val intent = Intent(this, PatientDashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
        // apply dark/light mode
        ThemeHelper.applyTheme(this)
    }
}