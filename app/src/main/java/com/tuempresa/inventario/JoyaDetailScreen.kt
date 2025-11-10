package com.tuempresa.inventario

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoyaDetailScreen(joya: Joya, onBack: () -> Unit, cartViewModel: CartViewModel) {
    val db = FirebaseFirestore.getInstance()
    var agregado by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf(false) }
    var comentarios by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var promedio by remember { mutableStateOf(0.0) }
    var cargando by remember { mutableStateOf(true) }

    // 🔹 Cargar los comentarios de la colección global "comentarios" según productoId
    LaunchedEffect(joya.id) {
        db.collection("comentarios")
            .whereEqualTo("productoId", joya.nombre)

            .get()
            .addOnSuccessListener { snapshot ->
                val lista = snapshot.documents.mapNotNull { it.data }
                comentarios = lista

                // Calcular promedio
                val calificaciones = lista.mapNotNull { (it["calificacion"] as? Long)?.toInt() }
                val filtradas = calificaciones.filter { it > 0 }
                promedio = if (filtradas.isNotEmpty()) filtradas.average() else 0.0

                cargando = false
            }
            .addOnFailureListener { cargando = false }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(joya.nombre) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                AsyncImage(
                    model = joya.imagen,
                    contentDescription = joya.nombre,
                    modifier = Modifier
                        .height(250.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(joya.nombre, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Text(joya.precio, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(joya.descripcion, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        cartViewModel.agregar(joya)
                        agregado = true
                        showSnackbar = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = CircleShape
                ) {
                    Text(if (agregado) "✅ Agregado al carrito" else "Agregar al carrito 💎")
                }

                Spacer(modifier = Modifier.height(24.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))

                if (cargando) {
                    CircularProgressIndicator()
                } else {
                    Text(
                        "⭐ Promedio de calificación: ${String.format("%.1f", promedio)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (comentarios.isEmpty()) {
                        Text("Sin comentarios aún.", fontSize = 15.sp)
                    } else {
                        Text("💬 Opiniones de compradores:", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // 🔸 Mostrar comentarios en tarjetas
            items(comentarios.size) { index ->
                val c = comentarios[index]
                val texto = c["comentario"] as? String ?: ""
                val calif = (c["calificacion"] as? Long)?.toInt() ?: 0
                val fecha = c["fecha"] as? String ?: ""

                Surface(
                    tonalElevation = 2.dp,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⭐".repeat(calif))
                            if (calif == 0) Text("Sin calificación")
                        }
                        Text(texto)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "🕓 $fecha",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                if (showSnackbar) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        if (showSnackbar) {
            // Colocamos el Snackbar dentro de un Box que ocupa toda la pantalla
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Snackbar(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("💎 Agregado al carrito")
                }
            }

            LaunchedEffect(Unit) {
                delay(1800)
                showSnackbar = false
            }
        }

    }
}
