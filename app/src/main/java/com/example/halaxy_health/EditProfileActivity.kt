package com.example.halaxy_health

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class EditProfileActivity : AppCompatActivity() {

    private val db by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtName = findViewById<EditText>(R.id.edtName)
        val edtSurname = findViewById<EditText>(R.id.edtSurname)
        val btnSave = findViewById<Button>(R.id.btnSave)

        val email = getSharedPreferences("halaxy_session", MODE_PRIVATE).getString("logged_email", "") ?: ""
        edtEmail.setText(email)

        if (email.isNotEmpty()) {
            lifecycleScope.launch {
                val user = db.userDao().getUserByEmail(email)
                if (user != null) {
                    edtName.setText(user.name)
                    edtSurname.setText(user.surname)
                }
            }
        }

        btnSave.setOnClickListener {
            val newName = edtName.text.toString().trim()
            val newSurname = edtSurname.text.toString().trim()

            if (newName.isEmpty() || newSurname.isEmpty()) {
                Toast.makeText(this, "Name and surname cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val user = db.userDao().getUserByEmail(email)
                if (user != null) {
                    db.userDao().insertUser(
                        user.copy(name = newName, surname = newSurname)
                    )
                    Toast.makeText(this@EditProfileActivity, "Profile updated", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}
