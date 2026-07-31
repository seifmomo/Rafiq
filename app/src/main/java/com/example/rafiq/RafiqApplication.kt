package com.example.rafiq

import android.app.Application
import android.content.res.Configuration
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale

@HiltAndroidApp
class RafiqApplication : Application() {

    override fun attachBaseContext(base: android.content.Context) {
        val lang = com.example.rafiq.util.LocaleManager.getPersistedLanguage(base)
        if (lang != "en") {
            val locale = Locale(lang)
            Locale.setDefault(locale)
            val config = Configuration(base.resources.configuration)
            config.setLocale(locale)
            super.attachBaseContext(base.createConfigurationContext(config))
        } else {
            super.attachBaseContext(base)
        }
    }
}
