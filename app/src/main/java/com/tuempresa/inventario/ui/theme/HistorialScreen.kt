package com.tuempresa.inventario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class Compra(
    val productos: List<Map<String, String>> = emptyList(),
    val total: String = "",
    val fecha: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(onBack: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var compras by remember { mutableStateOf(listOf<Compra>()) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("historial_compras")
                .document(uid)
                .collection("compras")
                .get()
                .addOnSuccessListener { result ->
                    compras = result.mapNotNull { it.toObject(Compra::class.java) }
                    cargando = false
                }
                .addOnFailureListener {
                    cargando = false
                }
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
            if (compras.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aún no tienes compras registradas 🛍️")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(compras) { compra ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("📅 ${compra.fecha}", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                compra.productos.forEach {
                                    Text("- ${it["nombre"]} (${it["precio"]})")
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("💰 Total: ${compra.total}", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
