package com.mice.smooth.login

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginRegistrationScreen(
    authViewModel: AuthViewModel = viewModel(),
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    var showPassword by remember { mutableStateOf(false) }
    val isLoginMode by authViewModel.isLoginMode.collectAsState()
    val email by authViewModel.email.collectAsState()
    val password by authViewModel.password.collectAsState()
    val confirmPassword by authViewModel.confirmPassword.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedContent(
                targetState = isLoginMode,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith
                            fadeOut(animationSpec = tween(300)) using
                            SizeTransform { initialSize, targetSize ->
                                if (targetState) {
                                    keyframes {
                                        IntSize(targetSize.width, initialSize.height) at 150
                                        durationMillis = 300
                                    }
                                } else {
                                    keyframes {
                                        IntSize(initialSize.width, targetSize.height) at 150
                                        durationMillis = 300
                                    }
                                }
                            }
                }, label = ""
            ) { isLogin ->
                Text(
                    text = if (isLogin) "欢迎回来" else "创建账户",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { authViewModel.updateEmail(it) },
                label = { Text("邮箱") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { authViewModel.updatePassword(it) },
                label = { Text("密码") },
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (showPassword) "隐藏密码" else "显示密码"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            )

            AnimatedVisibility(
                visible = !isLoginMode,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { authViewModel.updateConfirmPassword(it) },
                    label = { Text("确认密码") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .animateContentSize()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (isLoginMode) {
                        authViewModel.loginUser(
                            context = context,
                            onSuccess = {
                                onLoginSuccess() // 登录成功后调用回调
                            },
                            onError = { errorMessage ->
                                authViewModel.setErrorMessage(errorMessage)
                            }
                        )
                    } else {
                        authViewModel.registerUser(
                            onSuccess = { authViewModel.toggleMode() },
                            onError = { errorMessage ->
                                authViewModel.setErrorMessage(errorMessage)
                            }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .animateContentSize(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(text = if (isLoginMode) "登录" else "注册")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { authViewModel.toggleMode() },
                modifier = Modifier.animateContentSize()
            ) {
                Text(
                    text = if (isLoginMode) "需要一个账户？注册" else "已有账户？登录"
                )
            }

            errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

