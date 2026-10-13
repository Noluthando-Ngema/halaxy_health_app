package com.example.halaxy_health

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

    val db = AppDatabase.getDatabase(this)
    val sessionPrefs = getSharedPreferences("halaxy_session", MODE_PRIVATE)
    val themePrefs = getSharedPreferences("theme_prefs", MODE_PRIVATE)
    val email = sessionPrefs.getString("logged_email", "")!!

    val edtName = findViewById<EditText>(R.id.edtName)
    val edtSurname = findViewById<EditText>(R.id.edtSurname)
    val edtEmail = findViewById<EditText>(R.id.edtEmail)
    val edtPass = findViewById<EditText>(R.id.edtPassword)
    val spinPayment = findViewById<Spinner>(R.id.spinPayment)

    spinPayment.adapter = ArrayAdapter(
        this,
        android.R.layout.simple_spinner_item,
        listOf("Card - Visa ** 1234", "Medical Aid", "Cash", "EFT")
    )
    spinPayment.setSelection(themePrefs.getInt("payment_index", 0))

    lifecycleScope.launch {
        db.userDao().getUserByEmail(email)?.let {
            runOnUiThread {
                edtName.setText(it.name); edtSurname.setText(it.surname); edtEmail.setText(it.email)
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
        val newName = edtName.text.toString().trim()
        val newSurname = edtSurname.text.toString().trim()
        val newEmail = edtEmail.text.toString().trim()
        val newPass = edtPass.text.toString()

        if (!Validator.isValidEmail(newEmail)) {
            findViewById<TextView>(R.id.txtEmailErr).text="Invalid email format"; findViewById<TextView>(R.id.txtEmailErr).visibility=View.VISIBLE; return@setOnClickListener
        }
        if (newPass.isNotEmpty() && !Validator.isValidPassword(newPass)) {
            findViewById<TextView>(R.id.txtPassErr).text=Validator.getPasswordRequirements(); findViewById<TextView>(R.id.txtPassErr).visibility=View.VISIBLE; return@setOnClickListener
        }

        lifecycleScope.launch {
            // check one-time email conflict
            if (newEmail != email && (db.userDao().checkEmailExists(newEmail) != null || db.doctorDao().getDoctorByEmail(newEmail) != null)) {
                runOnUiThread { findViewById<TextView>(R.id.txtEmailErr).text="Email already used"; findViewById<TextView>(R.id.txtEmailErr).visibility=View.VISIBLE }
                return@launch
            }
            val user = db.userDao().getUserByEmail(email)!!
            val hashed = if (newPass.isNotEmpty()) PasswordHasher.hashPassword(newPass) else user.passwordHash
            db.userDao().updateUser(user.copy(name=newName, surname=newSurname, email=newEmail, passwordHash=hashed))
            themePrefs.edit().putInt("payment_index", spinPayment.selectedItemPosition).apply()
            sessionPrefs.edit().putString("logged_email", newEmail).apply()
            runOnUiThread { Toast.makeText(this@SettingsActivity, "Saved", Toast.LENGTH_SHORT).show() }
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