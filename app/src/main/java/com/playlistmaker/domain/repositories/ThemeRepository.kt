package com.playlistmaker.domain.repositories

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit


class ThemeRepository(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)

    var darkTheme: Boolean
        get() = sharedPreferences.getBoolean(THEME_SWITCHER, false)
        set(value) {
            sharedPreferences.edit { putBoolean(THEME_SWITCHER, value) }
            AppCompatDelegate.setDefaultNightMode(
                if (value) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

    companion object {
        private const val THEME_SWITCHER = "THEME_SWITCHER"
        private const val SHARED_PREFS = "SHARED_PREFS"
    }
}