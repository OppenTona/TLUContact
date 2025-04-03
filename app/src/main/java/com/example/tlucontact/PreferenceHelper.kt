package com.example.tlucontact

import android.accounts.AccountManager.KEY_PASSWORD
import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    fun saveUserId(userId: String) {
        sharedPreferences.edit().putString("USER_ID", userId).apply()
    }

    fun getUserId(): String? {
        return sharedPreferences.getString("USER_ID", null)
    }

    fun clearUserData() {
        sharedPreferences.edit().clear().apply()
    }
}