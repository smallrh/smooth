package com.mice.smooth.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class TokenUtil {
    fun saveAccessTokenToPreferences(context: Context, accessToken: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("Access-Token", accessToken)
        editor.apply()
    }
    fun getAccessTokenFromPreferences(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("Access-Token", null)
    }

    fun saveRefreshTokenToPreferences(context: Context, refreshToken: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("Refresh-Token", refreshToken)
        Log.d("refresh_token", refreshToken)
        editor.apply()
    }

    fun getRefreshTokenFromPreferences(context: Context): String?{
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("Refresh-Token", null)
    }
    fun clearTokens(context: Context) {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            remove("Access-Token")
            remove("Refresh-Token")
            apply()
        }
    }
}