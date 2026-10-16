package com.example.halaxy_health

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val db = AppDatabase.getDatabase(this)
        val sessionPrefs = getSharedPreferences("halaxy_session", MODE_PRIVATE)
        val themePrefs = getSharedPreferences("theme_prefs", MODE_PRIVATE)

        // FIXED: safe get, no !!
        val email = sessionPrefs.getString("logged_email", null)
        if (email.isNullOrEmpty()) {
            Toast.makeText(this, "No logged in user", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val edtName = findViewById<EditText>(R.id.edtName)
        val edtSurname = findViewById<EditText>(R.id.edtSurname)
        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtPass = findViewById<EditText>(R.id.edtPassword)
        val spinPayment = findViewById<Spinner>(R.id.spinPayment)
        val txtEmailErr = findViewById<TextView>(R.id.txtEmailErr)
        val txtPassErr = findViewById<TextView>(R.id.txtPassErr)

        spinPayment.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listOf("Card - Visa ** 1234", "Medical Aid", "Cash", "EFT")
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        spinPayment.setSelection(themePrefs.getInt("payment_index", 0))

        lifecycleScope.launch {
            val user = db.userDao().getUserByEmail(email)
            if (user != null) {
                runOnUiThread {
                    edtName.setText(user.name)
                    edtSurname.setText(user.surname)
                    edtEmail.setText(user.email)
                }
            }
        }

        findViewById<Button>(R.id.btnLight).setOnClickListener {
            themePrefs.edit().putBoolean("dark_mode", false).apply()
            ThemeHelper.applyTheme(this)
            recreate()
        }
        findViewById<Button>(R.id.btnDark).setOnClickListener {
            themePrefs.edit().putBoolean("dark_mode", true).apply()
            ThemeHelper.applyTheme(this)
            recreate()
        }

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            txtEmailErr.visibility = View.GONE
            txtPassErr.visibility = View.GONE

            val newName = edtName.text.toString().trim()
            val newSurname = edtSurname.text.toString().trim()
            val newEmail = edtEmail.text.toString().trim()
            val newPass = edtPass.text.toString()

            if (!Validator.isValidEmail(newEmail)) {
                txtEmailErr.text="Invalid email format"; txtEmailErr.visibility=View.VISIBLE; return@setOnClickListener
            }
            if (newPass.isNotEmpty() && !Validator.isValidPassword(newPass)) {
                txtPassErr.text=Validator.getPasswordRequirements(); txtPassErr.visibility=View.VISIBLE; return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    if (newEmail != email && (db.userDao().checkEmailExists(newEmail) != null || db.doctorDao().getDoctorByEmail(newEmail) != null)) {
                        runOnUiThread { txtEmailErr.text="Email already used"; txtEmailErr.visibility=View.VISIBLE }
                        return@launch
                    }
                    val currentUser = db.userDao().getUserByEmail(email)
                    if (currentUser == null) {
                        runOnUiThread { Toast.makeText(this@SettingsActivity, "User not found", Toast.LENGTH_SHORT).show() }
                        return@launch
                    }
                    val hashed = if (newPass.isNotEmpty()) PasswordHasher.hashPassword(newPass) else currentUser.passwordHash

                    // FIXED: safe update
                    db.userDao().updateUser(currentUser.copy(name=newName, surname=newSurname, email=newEmail, passwordHash=hashed))
                    themePrefs.edit().putInt("payment_index", spinPayment.selectedItemPosition).apply()
                    sessionPrefs.edit().putString("logged_email", newEmail).apply()

                    runOnUiThread {
                        Toast.makeText(this@SettingsActivity, "Saved", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread { Toast.makeText(this@SettingsActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show() }
                }
            }
        }

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            sessionPrefs.edit().clear().apply()
            startActivity(Intent(this, LoginActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK })
            finish()
        }

        ThemeHelper.applyTheme(this)
    }
}