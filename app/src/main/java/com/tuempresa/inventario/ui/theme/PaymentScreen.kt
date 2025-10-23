package com.tuempresa.inventario

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(onBack: () -> Unit, cartViewModel: CartViewModel, navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val coroutineScope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var tarjeta by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var tipoEntrega by remember { mutableStateOf("Tienda") }
    var direccion by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("💳 Pago") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Completa tus datos para finalizar la compra", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del titular") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = tarjeta,
                onValueChange = { tarjeta = it },
                label = { Text("Número de tarjeta") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text("Tipo de entrega", fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                listOf("Tienda", "Delivery").forEach { tipo ->
                    FilterChip(
                        selected = tipoEntrega == tipo,
                        onClick = { tipoEntrega = tipo },
                        label = { Text(tipo) }
                    )
                }
            }

// 🔹 Si elige Delivery, mostrar campo de dirección
            if (tipoEntrega == "Delivery") {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección de entrega") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = {
                    if (nombre.isBlank() || tarjeta.isBlank()) {
                        mensaje = "❌ Completa todos los campos"
                        return@Button
                    }
                    if (tipoEntrega == "Delivery" && direccion.isBlank()) {
                        mensaje = "❌ Ingresa la dirección de entrega"
                        return@Button
                    }

                    val usuario = auth.currentUser
                    if (usuario != null) {
                        val compra = hashMapOf(
                            "productos" to cartViewModel.carrito.map {
                                mapOf("nombre" to it.nombre, "precio" to it.precio)
                            },
                            "total" to "S/${"%.2f".format(cartViewModel.total())}",
                            "fecha" to SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()),
                            "tipoEntrega" to tipoEntrega,
                            "direccion" to if (tipoEntrega == "Delivery") direccion else "Recojo en tienda"
                        )

                        coroutineScope.launch {
                            db.collection("historial_compras")
                                .document(usuario.uid)
                                .collection("compras")
                                .add(compra)
                                .addOnSuccessListener {
                                    mensaje = "✅ Compra realizada con éxito"
                                    cartViewModel.vaciar()
                                }
                                .addOnFailureListener {
                                    mensaje = "❌ Error al registrar la compra"
                                }
                        }
                    } else {
                        mensaje = "❌ Debes iniciar sesión"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar compra")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(mensaje)
        }
    }
}
