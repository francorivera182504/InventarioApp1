package com.tuempresa.inventario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(
                            onRegisterClick = { navController.navigate("register") }
                        )
                    }
                    composable("register") {
                        RegisterScreen(
                            onLoginClick = { navController.navigate("login") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(onRegisterClick: () -> Unit) {
    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE0A800), // Mostaza
                        Color(0xFFD4AF37)  // Dorado
                    )
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Imagen del logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(220.dp)
                    .padding(bottom = 32.dp)
            )

            // Campo de correo
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo de contraseña
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de iniciar sesión
            Button(
                onClick = {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            message = if (task.isSuccessful) {
                                "✅ Login exitoso"
                            } else {
                                "❌ Error: ${task.exception?.message}"
                            }
                        }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar sesión")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para ir a registro
            TextButton(onClick = { onRegisterClick() }) {
                Text("¿No tienes cuenta? Crear una")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Mensaje de resultado
            Text(message)
        }
    }
}

@Composable
fun RegisterScreen(onLoginClick: () -> Unit) {
    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D1B2A),
                        Color(0xFF1B263B),
                        Color(color=0xFF000000)//
                    )
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Imagen del logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(220.dp)
                    .padding(bottom = 32.dp)
            )

            // Campo de correo
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo de contraseña
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de crear cuenta
            Button(
                onClick = {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            message = if (task.isSuccessful) {
                                "✅ Cuenta creada"
                            } else {
                                "❌ Error: ${task.exception?.message}"
                            }
                        }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear cuenta")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para volver al login
            TextButton(onClick = { onLoginClick() }) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Mensaje de resultado
            Text(message)
        }
    }
}
