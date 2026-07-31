package com.example.rafiq.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.tokenStore: DataStore<Preferences> by preferencesDataStore(name = "auth_tokens")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_EMAIL = stringPreferencesKey("user_email")
    }

    val getToken: Flow<String> = context.tokenStore.data.map { prefs ->
        prefs[Keys.ACCESS_TOKEN] ?: ""
    }

    val getUserId: Flow<String> = context.tokenStore.data.map { prefs ->
        prefs[Keys.USER_ID] ?: ""
    }

    val getUserEmail: Flow<String> = context.tokenStore.data.map { prefs ->
        prefs[Keys.USER_EMAIL] ?: ""
    }

    suspend fun saveToken(token: String, userId: String, email: String) {
        context.tokenStore.edit { prefs ->
            prefs[Keys.ACCESS_TOKEN] = token
            prefs[Keys.USER_ID] = userId
            prefs[Keys.USER_EMAIL] = email
        }
    }

    suspend fun clearToken() {
        context.tokenStore.edit { prefs ->
            prefs.clear()
        }
    }
}
