package com.android.client.ninjacat.core

import android.content.Context

/**
 * This class holds shared preferences and performs relating operations
 */
class SharedPrefs(context: Context) {
    val PREFS_FILENAME = "prefs"
    val prefs = context.getSharedPreferences(PREFS_FILENAME, 0)
    val AUTH_TOKEN = "authToken"
    val USER_LOGIN = "userLogin"

    var accessToken: String?
        get() = prefs.getString(AUTH_TOKEN, "")
        set(value) = prefs.edit().putString(AUTH_TOKEN, value).apply()

    var userLogin: String?
        get() = prefs.getString(USER_LOGIN, "")
        set(value) = prefs.edit().putString(USER_LOGIN, value).apply()
}

