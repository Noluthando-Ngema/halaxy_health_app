package com.example.halaxy_health

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        // get data from booking
        val amount = intent.getDoubleExtra("amount", 500.0)
        val appointmentId = intent.getIntExtra("appointmentId", -1)
        val doctorName = intent.getStringExtra("doctorName") ?: ""
        val dateTime = intent.getStringExtra("dateTime") ?: ""

        // show amount
        findViewById<TextView>(R.id.txtPaymentInfo).text = "Paying $doctorName - R$amount"
        findViewById<Button>(R.id.btnPay).text = "Pay R$amount"

        findViewById<Button>(R.id.btnPay).setOnClickListener {
            // simulate payment success

            // go to confirmation
            val intent = Intent(this, PaymentConfirmationActivity::class.java)
            intent.putExtra("appointmentId", appointmentId)
            intent.putExtra("doctorName", doctorName)
            intent.putExtra("dateTime", dateTime)
            intent.putExtra("amount", amount)
            startActivity(intent)
            finish()
        }
        // apply dark/light mode
        ThemeHelper.applyTheme(this)
    }
}