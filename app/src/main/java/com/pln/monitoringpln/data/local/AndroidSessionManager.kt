package com.pln.monitoringpln.data.local

import android.content.Context
import io.github.jan.supabase.gotrue.SessionManager

import io.github.jan.supabase.gotrue.user.UserSession
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AndroidSessionManager(context: Context) : SessionManager {
    private val prefs = context.getSharedPreferences("supabase_session", Context.MODE_PRIVATE)

    override suspend fun saveSession(session: UserSession) {
        val json = Json.encodeToString(session)
        prefs.edit().putString("session", json).apply()
    }

    override suspend fun loadSession(): UserSession? {
        val json = prefs.getString("session", null) ?: return null
        return try {
            Json.decodeFromString(json)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun deleteSession() {
        prefs.edit().remove("session").apply()
    }
}
