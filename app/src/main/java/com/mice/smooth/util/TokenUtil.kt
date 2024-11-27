package com.mice.smooth.util

import android.content.Context
import android.content.SharedPreferences

class TokenUtil {
    fun saveAccessTokenToPreferences(context: Context, accessToken: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("access_token", accessToken)
        editor.apply()
    }
    fun getAccessTokenFromPreferences(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("access_token", null)
    }

    fun saveRefreshTokenToPreferences(context: Context, refreshToken: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("refresh_token", refreshToken)
        editor.apply()
    }
    fun getRefreshTokenFromPreferences(context: Context): String?{
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("refresh_token", null)
    }
}