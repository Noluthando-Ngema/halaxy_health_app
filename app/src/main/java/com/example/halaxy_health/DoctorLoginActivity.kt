package com.example.halaxy_health

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.halaxy_health.AppDatabase
import com.example.halaxy_health.PasswordHasher
import com.example.halaxy_health.Validator
import kotlinx.coroutines.launch

class DoctorLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_login)

        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtPass = findViewById<EditText>(R.id.edtPassword)
        val btn = findViewById<Button>(R.id.btnLogin)
        val txtErr = findViewById<TextView>(R.id.txtLoginError)
        val txtEmailErr = findViewById<TextView>(R.id.txtEmailErrorLogin)

        val db = AppDatabase.getDatabase(this)

        btn.setOnClickListener {
            txtErr.visibility=View.GONE; txtEmailErr.visibility=View.GONE
            val email = edtEmail.text.toString().trim()
            val pass = edtPass.text.toString()

            if (!Validator.isValidEmail(email)) { txtEmailErr.text="Invalid email format"; txtEmailErr.visibility=View.VISIBLE; return@setOnClickListener }

            lifecycleScope.launch {
                val doc = db.doctorDao().getDoctorByEmail(email)
                if (doc == null || !PasswordHasher.verifyPassword(pass, doc.passwordHash)) {
                    runOnUiThread { txtErr.text="Email or password is incorrect"; txtErr.visibility=View.VISIBLE }
                    return@launch
                }
                if (!doc.isApproved) {
                    runOnUiThread { txtErr.text= if(doc.status=="PENDING") "Doctor account pending admin approval" else "Doctor account declined"; txtErr.visibility=View.VISIBLE }
                    return@launch
                }

                getSharedPreferences("halaxy_session", MODE_PRIVATE).edit().putString("logged_doctor", email).apply()

                runOnUiThread {
                    startActivity(Intent(this@DoctorLoginActivity, DoctorDashboardActivity::class.java))
                    finish()
                }
            }
        }
        findViewById<TextView>(R.id.txtGoSignup).setOnClickListener {
            startActivity(Intent(this, DoctorSignupActivity::class.java))
        }
        findViewById<Button>(R.id.btnAdminLogin).setOnClickListener {
            startActivity(Intent(this, AdminLoginActivity::class.java))
        }
    }
}