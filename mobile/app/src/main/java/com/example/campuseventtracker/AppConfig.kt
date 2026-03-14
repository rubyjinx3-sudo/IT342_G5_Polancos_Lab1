package com.example.campuseventtracker

import android.content.Context

object AppConfig {
    const val APP_NAME = "Campus Event Tracker"
    const val BASE_URL = "http://10.0.2.2:8080/api"
    const val USER_PREFS = "ced_user"
    const val PROFILE_PREFS = "ced_profile_prefs"
}

object ProfilePreferences {
    private fun avatarKey(email: String) = "avatar_$email"

    fun getAvatarUri(context: Context, email: String?): String? {
        if (email.isNullOrBlank()) return null
        val prefs = context.getSharedPreferences(AppConfig.PROFILE_PREFS, Context.MODE_PRIVATE)
        return prefs.getString(avatarKey(email), null)
    }

    fun saveAvatarUri(context: Context, email: String?, uri: String) {
        if (email.isNullOrBlank()) return
        val prefs = context.getSharedPreferences(AppConfig.PROFILE_PREFS, Context.MODE_PRIVATE)
        prefs.edit().putString(avatarKey(email), uri).apply()
    }
}
