package com.mice.smooth.util

import android.content.Context
import androidx.navigation.NavController
import com.mice.smooth.api.RetrofitClient

class RefreshTokenUtil {
    suspend fun refreshAccessToken(
        refreshToken: String,
        context: Context,
        navController: NavController,
        tokenUtil: TokenUtil
    ): String? {
        return try {
            val response = RetrofitClient.apiService.refreshToken(refreshToken)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.code == 200) {
                    // 存储新的 access_token
                    val newAccessToken = apiResponse.data.access_token
                    newAccessToken.let { tokenUtil.saveAccessTokenToPreferences(context, it) }
                    newAccessToken
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}