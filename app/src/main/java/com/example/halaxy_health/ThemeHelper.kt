package com.example.halaxy_health

import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

object ThemeHelper {
    fun applyTheme(activity: AppCompatActivity) {
        val isDark = activity.getSharedPreferences("theme_prefs", MODE_PRIVATE).getBoolean("dark_mode", false)

        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        val root = activity.findViewById<View>(R.id.rootLayout)
            ?: activity.findViewById<View>(R.id.rootSettings)
            ?: activity.window.decorView.rootView

        val bgColor = if (isDark) Color.parseColor("#121212") else Color.WHITE
        root.setBackgroundColor(bgColor)

        tintAllViews(root, isDark)
    }

    private fun tintAllViews(view: View, isDark: Boolean) {
        val textColor = if (isDark) Color.WHITE else Color.BLACK
        val cardColor = if (isDark) Color.parseColor("#2D2D2D") else Color.WHITE
        val inputColor = if (isDark) Color.parseColor("#333333") else Color.WHITE

        when (view) {
            is Button -> {
                // keep maroon/white as is
                return
            }
            is EditText -> {
                view.setBackgroundColor(inputColor)
                view.setTextColor(textColor)
                view.setHintTextColor(if (isDark) Color.GRAY else Color.parseColor("#888888"))
            }
            is TextView -> {
                // Only change black text to white, leaves white text on maroon
                if (view.currentTextColor == Color.BLACK || view.currentTextColor == Color.parseColor("#FF000000")) {
                    view.setTextColor(textColor)
                }
            }
            is ViewGroup -> {
                // Change card backgrounds
                if (view.id == R.id.cardMyAppointments ||
                    view.id == R.id.cardMakeBooking2 ||
                    view.id == R.id.cardEditProfile ||
                    view.id == R.id.cardLearningCentre) {
                    view.setBackgroundColor(cardColor)
                }

                for (i in 0 until view.childCount) {
                    tintAllViews(view.getChildAt(i), isDark)
                }
            }
        }
    }
}