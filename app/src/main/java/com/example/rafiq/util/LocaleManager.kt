package com.example.rafiq.util

import android.content.Context

object LocaleManager {

    private const val PREFS_NAME = "rafiq_locale"
    private const val KEY_LANGUAGE = "language_code"

    fun persistLanguage(context: Context, languageCode: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANGUAGE, languageCode)
            .commit()
    }

    fun getPersistedLanguage(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE, "en") ?: "en"
    }
}
