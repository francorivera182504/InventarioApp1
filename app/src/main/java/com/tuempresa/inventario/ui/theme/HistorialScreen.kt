package com.tuempresa.inventario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(onBack: () -> Unit, navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val usuario = auth.currentUser
    var historial by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (usuario != null) {
            db.collection("historial_compras")
                .document(usuario.uid)
                .collection("compras")
                .get()
                .addOnSuccessListener { result ->
                    historial = result.map { it.data }
                    cargando = false
                }
                .addOnFailureListener { cargando = false }
        } else {
            cargando = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("🧾 Historial de compras") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (cargando) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(historial.size) { index ->
                    val compra = historial[index]
                    var calificacion by remember { mutableStateOf((compra["calificacion"] as? Long)?.toInt() ?: 0) }

                    Surface(
                        tonalElevation = 4.dp,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("🛍️ Total: ${compra["total"]}")
                            Text("📅 Fecha: ${compra["fecha"]}")
                            Text("🚚 Entrega: ${compra["tipoEntrega"]}")
                            if (compra["tipoEntrega"] == "Delivery") {
                                Text("🏠 Dirección: ${compra["direccion"]}")
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("⭐ Califica tu compra:")
                            Row {
                                (1..5).forEach { star ->
                                    IconButton(
                                        onClick = {
                                            calificacion = star
                                            // 🔥 Guardar calificación en Firestore
                                            usuario?.let {
                                                db.collection("historial_compras")
                                                    .document(it.uid)
                                                    .collection("compras")
                                                    .whereEqualTo("fecha", compra["fecha"])
                                                    .get()
                                                    .addOnSuccessListener { snapshot ->
                                                        if (!snapshot.isEmpty) {
                                                            val docId = snapshot.documents[0].id
                                                            db.collection("historial_compras")
                                                                .document(it.uid)
                                                                .collection("compras")
                                                                .document(docId)
                                                                .update("calificacion", star)
                                                        }
                                                    }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (star <= calificacion)
                                                Icons.Filled.Star else Icons.Outlined.Star,
                                            contentDescription = "Estrella $star",
                                            tint = if (star <= calificacion)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
