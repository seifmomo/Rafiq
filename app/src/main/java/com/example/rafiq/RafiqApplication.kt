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

    override fun onCreate() {
        super.onCreate()
        org.osmdroid.config.Configuration.getInstance().apply {
            // OSM tile usage policy: a real, identifying User-Agent is required
            // (bare package names / "osmdroid" get a 403 from tile.openstreetmap.org)
            userAgentValue = "RAFIQ-Android/1.0 (accessibility companion for people with disabilities; contact: rafiq.app.demo@gmail.com)"
            osmdroidBasePath = org.osmdroid.config.Configuration.getInstance().osmdroidBasePath
            osmdroidTileCache = java.io.File(cacheDir, "osmdroid")
        }
    }
}
