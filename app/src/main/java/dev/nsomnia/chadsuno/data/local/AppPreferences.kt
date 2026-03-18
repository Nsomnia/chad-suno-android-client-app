package dev.nsomnia.chadsuno.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "chadsuno_prefs")

class AppPreferences(private val context: Context) {
    
    companion object {
        private val COOKIE_KEY = stringPreferencesKey("suno_cookie")
        private val API_BASE_URL_KEY = stringPreferencesKey("api_base_url")
        private val THEME_KEY = stringPreferencesKey("theme_mode")
    }

    val cookie: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[COOKIE_KEY]
    }

    val apiBaseUrl: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[API_BASE_URL_KEY]
    }

    val themeMode: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[THEME_KEY]
    }

    suspend fun saveCookie(cookie: String) {
        context.dataStore.edit { prefs ->
            prefs[COOKIE_KEY] = cookie
        }
    }

    suspend fun saveApiBaseUrl(url: String) {
        context.dataStore.edit { prefs ->
            prefs[API_BASE_URL_KEY] = url
        }
    }

    suspend fun saveThemeMode(theme: String) {
        context.dataStore.edit { prefs ->
            prefs[THEME_KEY] = theme
        }
    }

    suspend fun clearCookie() {
        context.dataStore.edit { prefs ->
            prefs.remove(COOKIE_KEY)
        }
    }
}
