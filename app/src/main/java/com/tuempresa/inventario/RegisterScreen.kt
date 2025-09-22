package com.tuapp.inventario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RegisterScreen(onBackToLogin: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE0A800),
                        Color(0xFFFFD54F)
                    )
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Crear cuenta", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(24.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña (mín. 6)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // validaciones simples
                    if (email.isBlank() || password.length < 6) {
                        message = "Introduce email válido y contraseña (mín. 6 caracteres)"
                        return@Button
                    }

                    loading = true
                    // 1) crear usuario en Firebase Auth
                    auth.createUserWithEmailAndPassword(email.trim(), password)
                        .addOnCompleteListener { task ->
                            loading = false
                            if (task.isSuccessful) {
                                // 2) si creación exitosa, guardar datos en Firestore
                                val userId = auth.currentUser?.uid
                                val userMap = mapOf(
                                    "email" to email.trim(),
                                    "createdAt" to System.currentTimeMillis()
                                )
                                if (userId != null) {
                                    db.collection("users")
                                        .document(userId)
                                        .set(userMap)                 // <-- aquí se devuelve un Task
                                        .addOnSuccessListener {
                                            message = "✅ Cuenta creada y guardada en Firestore"
                                        }
                                        .addOnFailureListener { e ->
                                            message = "❌ Error al guardar en Firestore: ${e.message}"
                                        }
                                } else {
                                    message = "✅ Cuenta creada (uid no disponible)"
                                }
                            } else {
                                message = "❌ Error al crear cuenta: ${task.exception?.message}"
                            }
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(50.dp)
            ) {
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Registrar")
            }

            TextButton(onClick = { onBackToLogin() }, modifier = Modifier.padding(top = 12.dp)) {
                Text("¿Ya tienes cuenta? Iniciar sesión")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = message, color = Color.White, modifier = Modifier.padding(top = 12.dp))
        }
    }
}
