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

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val txtLoginError = findViewById<TextView>(R.id.txtLoginError)
        val txtEmailError = findViewById<TextView>(R.id.txtEmailErrorLogin)
        val txtPassError = findViewById<TextView>(R.id.txtPasswordErrorLogin)

        val db = AppDatabase.getDatabase(this)

        btnLogin.setOnClickListener {
            txtLoginError.visibility = View.GONE
            txtEmailError.visibility = View.GONE
            txtPassError.visibility = View.GONE

            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString()

            if (!Validator.isValidEmail(email)) {
                txtEmailError.text = "Invalid email format"
                txtEmailError.visibility = View.VISIBLE
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                txtPassError.text = "Password required"
                txtPassError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val user = db.userDao().getUserByEmail(email)
                    if (user == null || !PasswordHasher.verifyPassword(password, user.passwordHash)) {
                        runOnUiThread {
                            txtLoginError.text = "Email or password is incorrect"
                            txtLoginError.visibility = View.VISIBLE
                        }
                        return@launch
                    }
                    if (!user.isApproved) {
                        runOnUiThread {
                            txtLoginError.text = if (user.status == "PENDING") "Account pending admin approval" else "Account declined by admin"
                            txtLoginError.visibility = View.VISIBLE
                        }
                        return@launch
                    }

                    getSharedPreferences("halaxy_session", MODE_PRIVATE).edit()
                        .putString("logged_email", email).apply()

                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, PatientDashboardActivity::class.java))
                        finish()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        txtLoginError.text = "Try again"
                        txtLoginError.visibility = View.VISIBLE
                    }
                }
            }
        }

        findViewById<TextView>(R.id.txtGoSignup).setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
        findViewById<Button>(R.id.btnAdminLogin).setOnClickListener {
            startActivity(Intent(this, AdminLoginActivity::class.java))
        }
        findViewById<TextView>(R.id.txtDoctorSignup).setOnClickListener {
            startActivity(Intent(this, DoctorSignupActivity::class.java))
        }
    }
}
