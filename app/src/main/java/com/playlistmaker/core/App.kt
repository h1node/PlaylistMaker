package com.playlistmaker.core

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate


class App : Application() {
    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        val sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        darkTheme = sharedPreferences.getBoolean(THEME_SWITCHER, false)

        switchTheme(darkTheme)
    }

    fun switchTheme(darkEnabled: Boolean) {
        darkTheme = darkEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        val editor = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).edit()
        editor.putBoolean(THEME_SWITCHER, darkEnabled)
        editor.apply()
    }

    companion object {
        const val THEME_SWITCHER = "THEME_SWITCHER"
        const val SHARED_PREFS = "SHARED_PREFS"
    }
}