package com.example.halaxy_health

import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import android.widget.*
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

object ThemeHelper {
    fun applyTheme(activity: AppCompatActivity) {
        val isDark = activity.getSharedPreferences("theme_prefs", MODE_PRIVATE).getBoolean("dark_mode", false)
        val root = activity.findViewById<View>(R.id.rootLayout) ?: activity.findViewById<View>(R.id.rootSettings) ?: activity.window.decorView

        if (isDark) {
            root.setBackgroundColor(Color.parseColor("#121212"))
            // change all TextViews
            applyTextColor(activity.window.decorView, Color.WHITE)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            root.setBackgroundColor(Color.WHITE)
            applyTextColor(activity.window.decorView, Color.BLACK)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
    private fun applyTextColor(view: View, color: Int) {
        if (view is ViewGroup) { for (i in 0 until view.childCount) applyTextColor(view.getChildAt(i), color) }
        else if (view is TextView) { if (view.currentTextColor != Color.parseColor("#4A0D12")) view.setTextColor(color) } // keep maroon buttons white
    }
}