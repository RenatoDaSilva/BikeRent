package com.bike.rent

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

class SessionManager(private val context: Context) {
    companion object {
        val USER_HASH_KEY = stringPreferencesKey("user_hash")
    }

    val userHash: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_HASH_KEY]
        }

    suspend fun saveHash(hash: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_HASH_KEY] = hash
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_HASH_KEY)
        }
    }
}
