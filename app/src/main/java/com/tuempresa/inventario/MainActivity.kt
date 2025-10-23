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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.tuempresa.inventario.JoyaDetailScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()

                // ✅ Crear solo una vez el ViewModel aquí
                val cartViewModel: CartViewModel = viewModel()

                NavHost(navController = navController, startDestination = "login") {
                    // Pantalla de inicio de sesión
                    composable("login") {
                        LoginScreen(
                            onRegisterClick = { navController.navigate("register") },
                            onLoginSuccess = { navController.navigate("catalog") }
                        )
                    }
                    composable("payment") {
                        PaymentScreen(
                            onBack = { navController.popBackStack() },
                            cartViewModel = cartViewModel,
                            navController = navController
                        )

                    }

                    // Pantalla de registro
                    composable("register") {
                        RegisterScreen(
                            onLoginClick = { navController.navigate("login") }
                        )
                    }

                    // Catálogo de joyas
                    composable("catalog") {
                        var joyaSeleccionada by remember { mutableStateOf<Joya?>(null) }

                        if (joyaSeleccionada == null) {
                            JoyasCatalogScreen(
                                onJoyaClick = { joyaSeleccionada = it },
                                cartViewModel = cartViewModel,
                                navController = navController // 👈 AÑADE ESTA LÍNEA
                            )
                        } else {
                            JoyaDetailScreen(
                                joya = joyaSeleccionada!!,
                                onBack = { joyaSeleccionada = null },
                                cartViewModel = cartViewModel
                            )
                        }

                    }
                    composable("historial") {
                        HistorialScreen(
                            onBack = { navController.popBackStack() },
                            navController = navController
                        )
                    }

                    // Pantalla del mapa 📍
                    composable("map") {
                        MapScreen(onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(onRegisterClick: () -> Unit, onLoginSuccess: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFE0A800), Color(0xFFD4AF37))
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(220.dp).padding(bottom = 32.dp)
            )

            TextField(value = email, onValueChange = { email = it }, label = { Text("Correo") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(24.dp))
            TextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful) onLoginSuccess()
                        else message = "❌ Error: ${task.exception?.message}"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Iniciar sesión") }

            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = { onRegisterClick() }) { Text("¿No tienes cuenta? Crear una") }
            Spacer(modifier = Modifier.height(8.dp))
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
                    colors = listOf(Color(0xFF0D1B2A), Color(0xFF1B263B), Color(0xFF000000))
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(220.dp).padding(bottom = 32.dp)
            )

            TextField(value = email, onValueChange = { email = it }, label = { Text("Correo") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(24.dp))
            TextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        message = if (task.isSuccessful) "✅ Cuenta creada correctamente"
                        else "❌ Error: ${task.exception?.message}"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Crear cuenta") }

            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = { onLoginClick() }) { Text("¿Ya tienes cuenta? Inicia sesión") }
            Spacer(modifier = Modifier.height(8.dp))
            Text(message)
        }
    }
}
