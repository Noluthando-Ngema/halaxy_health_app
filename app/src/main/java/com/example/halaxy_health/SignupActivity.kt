package com.example.halaxy_health

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.halaxy_health.AppDatabase
import com.example.halaxy_health.User
import com.example.halaxy_health.PasswordHasher
import com.example.halaxy_health.Validator
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val edtName = findViewById<EditText>(R.id.edtName)
        val edtSurname = findViewById<EditText>(R.id.edtSurname)
        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val btnSignup = findViewById<Button>(R.id.btnSignup)

        val txtNameError = findViewById<TextView>(R.id.txtNameError)
        val txtSurnameError = findViewById<TextView>(R.id.txtSurnameError)
        val txtEmailError = findViewById<TextView>(R.id.txtEmailErrorSignup)
        val txtPassError = findViewById<TextView>(R.id.txtPasswordErrorSignup)

        val db = AppDatabase.getDatabase(this)

        btnSignup.setOnClickListener {
            txtNameError.visibility = View.GONE; txtSurnameError.visibility = View.GONE
            txtEmailError.visibility = View.GONE; txtPassError.visibility = View.GONE

            val name = edtName.text.toString().trim()
            val surname = edtSurname.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString()

            var hasError = false
            if (name.isEmpty()) { txtNameError.text="Name required"; txtNameError.visibility=View.VISIBLE; hasError=true }
            if (surname.isEmpty()) { txtSurnameError.text="Surname required"; txtSurnameError.visibility=View.VISIBLE; hasError=true }
            if (!Validator.isValidEmail(email)) { txtEmailError.text="Invalid email format"; txtEmailError.visibility=View.VISIBLE; hasError=true }
            if (!Validator.isValidPassword(password)) { txtPassError.text=Validator.getPasswordRequirements(); txtPassError.visibility=View.VISIBLE; hasError=true }
            if (hasError) return@setOnClickListener

            lifecycleScope.launch {
                try {
                    // ONE-TIME SIGNUP LOCK - check both tables
                    if (db.userDao().checkEmailExists(email) != null || db.doctorDao().getDoctorByEmail(email) != null) {
                        runOnUiThread { txtEmailError.text="Email already signed up. Single-only signup."; txtEmailError.visibility=View.VISIBLE }
                        return@launch
                    }

                    val hashed = PasswordHasher.hashPassword(password)
                    db.userDao().insertUser(User(name=name, surname=surname, email=email, passwordHash=hashed, isApproved=true, status="APPROVED"))

                    runOnUiThread {
                        Toast.makeText(this@SignupActivity, "Signup sent for admin approval. Please login after approval.", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                        finish()
                    }
                } catch (e: Exception) {
                    runOnUiThread { Toast.makeText(this@SignupActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show() }
                }
            }
        }

        findViewById<TextView>(R.id.txtGoLogin).setOnClickListener { finish() }
        findViewById<TextView>(R.id.txtDoctorSignup2).setOnClickListener {
            startActivity(Intent(this, DoctorSignupActivity::class.java))
        }
    }
}