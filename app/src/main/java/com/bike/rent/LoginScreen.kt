package com.bike.rent

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var cpf by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = cpf,
            onValueChange = { if (it.all { char -> char.isDigit() }) cpf = it },
            label = { Text("CPF") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (cpf.isBlank() || senha.isBlank()) {
                    Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                scope.launch {
                    isLoading = true
                    try {
                        val input = cpf + senha + com.bike.rent.BuildConfig.SUGAR
                        val hash = input.md5()
                        Log.d("LoginScreen", "Attempting login for CPF: $cpf")
                        
                        val response = RetrofitClient.apiService.login(cpf = cpf, hash = hash)
                        Log.d("LoginScreen", "Response code: ${response.code()}")
                        
                        if (response.isSuccessful) {
                            val loginResponse = response.body()
                            Log.d("LoginScreen", "Response body: $loginResponse")
                            if (loginResponse?.code == 200 && loginResponse.msg == "OK") {
                                sessionManager.saveHash(hash)
                                // Wait for the hash to be updated in the Flow if necessary, 
                                // but DataStore.edit is a suspend function that completes after writing.
                                Log.d("LoginScreen", "Hash saved successfully, navigating to Home")
                                onLoginSuccess()
                            } else if (loginResponse?.code == 401 && loginResponse.msg == "Unauthorized") {
                                Toast.makeText(context, "CPF ou senha inválidos", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Erro: ${loginResponse?.msg}", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            if (response.code() == 401) {
                                Toast.makeText(context, "CPF ou senha inválidos", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Erro no servidor: ${response.code()}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, "Falha na conexão (${e.javaClass.simpleName}): ${e.message ?: "Erro desconhecido"}", Toast.LENGTH_SHORT).show()
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Entrar")
            }
        }
    }
}
