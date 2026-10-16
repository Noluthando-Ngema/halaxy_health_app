package com.example.halaxy_health

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class DoctorSettingsActivity : AppCompatActivity() {

    private var photoIndex = 0
    private val photos = listOf(R.drawable.doctor1, R.drawable.doctor2, R.drawable.doctor3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_settings)

        val db = AppDatabase.getDatabase(this)
        val sessionPrefs = getSharedPreferences("halaxy_session", MODE_PRIVATE)
        val themePrefs = getSharedPreferences("theme_prefs", MODE_PRIVATE)
        val photoPrefs = getSharedPreferences("doctor_photo_prefs", MODE_PRIVATE)
        val email = sessionPrefs.getString("logged_doctor", "")!!

        val edtName = findViewById<EditText>(R.id.edtName)
        val edtSurname = findViewById<EditText>(R.id.edtSurname)
        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtPass = findViewById<EditText>(R.id.edtPassword)
        val imgProfile = findViewById<ImageView>(R.id.imgProfile)

        // load saved photo index
        photoIndex = photoPrefs.getInt("photo_index_$email", 0) % photos.size
        imgProfile.setImageResource(photos[photoIndex])

        findViewById<Button>(R.id.btnChangePhoto).setOnClickListener {
            photoIndex = (photoIndex + 1) % photos.size
            imgProfile.setImageResource(photos[photoIndex])
            photoPrefs.edit().putInt("photo_index_$email", photoIndex).apply()
            Toast.makeText(this, "Profile photo updated", Toast.LENGTH_SHORT).show()
        }

        lifecycleScope.launch {
            db.doctorDao().getDoctorByEmail(email)?.let { doc ->
                runOnUiThread {
                    edtName.setText(doc.name)
                    edtSurname.setText(doc.surname)
                    edtEmail.setText(doc.email)
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
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (newPass.isNotEmpty() && !Validator.isValidPassword(newPass)) {
                Toast.makeText(this, Validator.getPasswordRequirements(), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val doc = db.doctorDao().getDoctorByEmail(email)
                if (doc != null) {
                    val hashed = if (newPass.isNotEmpty()) PasswordHasher.hashPassword(newPass) else doc.passwordHash
                    val updated = doc.copy(
                        name = newName,
                        surname = newSurname,
                        email = newEmail,
                        passwordHash = hashed
                    )
                    db.doctorDao().updateDoctor(updated)
                    sessionPrefs.edit().putString("logged_doctor", newEmail).apply()
                    runOnUiThread {
                        Toast.makeText(this@DoctorSettingsActivity, "Settings saved successfully", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            sessionPrefs.edit().clear().apply()
            val intent = Intent(this, DoctorLoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        ThemeHelper.applyTheme(this)
    }
}
