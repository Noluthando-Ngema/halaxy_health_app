package com.example.halaxy_health

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class DoctorSignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_signup)

        val edtName = findViewById<EditText>(R.id.edtName)
        val edtSurname = findViewById<EditText>(R.id.edtSurname)
        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtSpec = findViewById<EditText>(R.id.edtSpec)
        val edtPass = findViewById<EditText>(R.id.edtPassword)
        val btn = findViewById<Button>(R.id.btnSignup)

        val txtNameErr = findViewById<TextView>(R.id.txtNameError)
        val txtSurnameErr = findViewById<TextView>(R.id.txtSurnameError)
        val txtEmailErr = findViewById<TextView>(R.id.txtEmailError)
        val txtPassErr = findViewById<TextView>(R.id.txtPassError)

        val db = AppDatabase.getDatabase(this)

        btn.setOnClickListener {
            txtNameErr.visibility = View.GONE
            txtSurnameErr.visibility = View.GONE
            txtEmailErr.visibility = View.GONE
            txtPassErr.visibility = View.GONE

            val name = edtName.text.toString().trim()
            val surname = edtSurname.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val spec = edtSpec.text.toString().trim()
            val pass = edtPass.text.toString()

            var hasError = false
            if (name.isEmpty()) { txtNameErr.text = "Name required"; txtNameErr.visibility = View.VISIBLE; hasError = true }
            if (surname.isEmpty()) { txtSurnameErr.text = "Surname required"; txtSurnameErr.visibility = View.VISIBLE; hasError = true }
            if (!Validator.isValidEmail(email)) { txtEmailErr.text = "Invalid email format. Example: jane.smith@clinic.com"; txtEmailErr.visibility = View.VISIBLE; hasError = true }
            if (!Validator.isValidPassword(pass)) { txtPassErr.text = Validator.getPasswordRequirements(); txtPassErr.visibility = View.VISIBLE; hasError = true }
            if (hasError) return@setOnClickListener

            lifecycleScope.launch {
                if (db.doctorDao().getDoctorByEmail(email) != null || db.userDao().checkEmailExists(email) != null) {
                    runOnUiThread { txtEmailErr.text = "Email already signed up."; txtEmailErr.visibility = View.VISIBLE }
                    return@launch
                }

                val hashed = PasswordHasher.hashPassword(pass)

                db.doctorDao().insertDoctor(
                    Doctor(
                        name = name,
                        surname = surname,
                        fullName = "Dr. $name $surname",
                        email = email,
                        passwordHash = hashed,
                        specialization = spec.ifEmpty { "General Physician" },
                        isApproved = false,
                        status = "PENDING"
                    )
                )

                runOnUiThread {
                    Toast.makeText(this@DoctorSignupActivity, "Doctor signup sent for admin approval", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@DoctorSignupActivity, DoctorLoginActivity::class.java))
                    finish()
                }
            }
        }

        findViewById<TextView>(R.id.txtGoLogin).setOnClickListener {
            startActivity(Intent(this, DoctorLoginActivity::class.java))
        }
    }
}
