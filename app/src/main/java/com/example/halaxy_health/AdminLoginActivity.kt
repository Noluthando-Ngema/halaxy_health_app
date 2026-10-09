package com.example.halaxy_health

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.halaxy_health.PasswordHasher
import com.example.halaxy_health.Validator

class AdminLoginActivity : AppCompatActivity() {
    // default admin
    private val ADMIN_EMAIL = "admin@gmail.com"
    private val ADMIN_PASS_PLAIN = "Admin123!"
    private val ADMIN_HASH by lazy { PasswordHasher.hashPassword(ADMIN_PASS_PLAIN) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)

        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtPass = findViewById<EditText>(R.id.edtPassword)
        val btn = findViewById<Button>(R.id.btnLogin)
        val txtEmailErr = findViewById<TextView>(R.id.txtEmailError)
        val txtPassErr = findViewById<TextView>(R.id.txtPassError)
        val txtLoginErr = findViewById<TextView>(R.id.txtLoginError)

        // prefill for you
        edtEmail.setText(ADMIN_EMAIL)
        edtPass.setText(ADMIN_PASS_PLAIN)

        btn.setOnClickListener {
            txtEmailErr.visibility = View.GONE
            txtLoginErr.visibility = View.GONE
            txtPassErr.visibility = View.GONE

            val email = edtEmail.text.toString().trim()
            val pass = edtPass.text.toString()

            if (!Validator.isValidEmail(email)) {
                txtEmailErr.text = "Invalid email format"
                txtEmailErr.visibility = View.VISIBLE
                return@setOnClickListener
            }
            if (email != ADMIN_EMAIL) {
                txtLoginErr.text = "Email or password is incorrect"
                txtLoginErr.visibility = View.VISIBLE
                return@setOnClickListener
            }
            // verify with hash (even though it's default)
            if (pass != ADMIN_PASS_PLAIN && !PasswordHasher.verifyPassword(pass, ADMIN_HASH)) {
                // allow plain compare for default
                if (pass != ADMIN_PASS_PLAIN) {
                    txtLoginErr.text = "Email or password is incorrect"
                    txtLoginErr.visibility = View.VISIBLE
                    return@setOnClickListener
                }
            }

            // success
            getSharedPreferences("halaxy_session", MODE_PRIVATE).edit()
                .putString("logged_admin", ADMIN_EMAIL).apply()

            Toast.makeText(this, "Welcome Admin", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, AdminDashboardActivity::class.java))
            finish()
        }

        findViewById<TextView>(R.id.txtBack).setOnClickListener { finish() }
    }
}