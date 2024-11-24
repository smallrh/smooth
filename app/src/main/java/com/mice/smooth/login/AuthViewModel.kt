package com.mice.smooth.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

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

    fun login(onSuccess: () -> Unit) {
        _isLoading.value = true
        // 模拟网络请求
        // 实际应用中，这里应该调用真实的登录 API
        if (_email.value.isNotBlank() && _password.value.isNotBlank()) {
            // 登录成功
            _isLoading.value = false
            onSuccess()
        } else {
            // 登录失败
            _isLoading.value = false
            _errorMessage.value = "邮箱或密码不能为空"
        }
    }

    fun register(onSuccess: () -> Unit) {
        _isLoading.value = true
        // 模拟网络请求
        // 实际应用中，这里应该调用真实的注册 API
        if (_email.value.isNotBlank() && _password.value.isNotBlank() && _password.value == _confirmPassword.value) {
            // 注册成功
            _isLoading.value = false
            onSuccess()
        } else {
            // 注册失败
            _isLoading.value = false
            _errorMessage.value = "请确保所有字段都已填写，且两次输入的密码相同"
        }
    }
}

