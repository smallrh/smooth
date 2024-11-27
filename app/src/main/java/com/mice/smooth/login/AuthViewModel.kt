package com.mice.smooth.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mice.smooth.api.RetrofitClient
import com.mice.smooth.api.UserBodyRequest
import com.mice.smooth.util.TokenUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword = _confirmPassword.asStateFlow()

    private val _isLoginMode = MutableStateFlow(true)
    val isLoginMode = _isLoginMode.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()
    private val tokenUtil = TokenUtil()

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun updateConfirmPassword(newConfirmPassword: String) {
        _confirmPassword.value = newConfirmPassword
    }

    fun toggleMode() {
        _isLoginMode.value = !_isLoginMode.value
        _email.value = ""
        _password.value = ""
        _confirmPassword.value = ""
        _errorMessage.value = null
    }

    fun loginUser(context: Context, onSuccess: () -> Unit, onError: (String) -> Unit) {
        _isLoading.value = true
        val request = UserBodyRequest(_email.value, _password.value)
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.login(request)
                if (response.isSuccessful) {
                    // 从响应头中获取 Token
                    val accessToken = response.headers()["Access-Token"]
                    val refreshToken = response.headers()["Refresh-Token"]

                    if (accessToken != null && refreshToken != null) {
                        // 存储 Token 到本地
                        tokenUtil.saveAccessTokenToPreferences(context, accessToken)
                        tokenUtil.saveRefreshTokenToPreferences(context, refreshToken)
                        onSuccess() // 登录成功后调用 onSuccess
                    } else {
                        onError("登录失败，未收到 Token")
                    }
                } else {
                    onError("登录失败，账号或密码错误")
                }
            } catch (e: Exception) {
                Log.e("LoginError", "Exception during login: ${e.localizedMessage}", e)
                onError("网络错误: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun registerUser(onSuccess: () -> Unit, onError: (String) -> Unit) {
        _isLoading.value = true
        val request = UserBodyRequest(_email.value, _password.value)
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.register(request)
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.code == 200) {
                        onSuccess()
                    } else {
                        onError(apiResponse?.message ?: "注册失败")
                    }
                } else {
                    onError("注册失败: ${response.message()}")
                }
            } catch (e: Exception) {
                onError("网络错误: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setErrorMessage(errorMessage: String) {
        _errorMessage.value = errorMessage
    }
}