package com.tuempresa.inventario

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.* // Importa todos los iconos de Filled
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore

// Suponiendo que tienes un recurso R.drawable.ic_search o usas Icons.Filled.Search
// Si no quieres depender de archivos locales para los iconos, usa el import anterior:
// import androidx.compose.material.icons.filled.Search

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoyasCatalogScreen(
    onJoyaClick: (Joya) -> Unit,
    cartViewModel: CartViewModel,
    navController: NavController
) {
    val db = FirebaseFirestore.getInstance()
    var joyas by remember { mutableStateOf(listOf<Joya>()) }
    var cargando by remember { mutableStateOf(true) }
    var mostrarCarrito by remember { mutableStateOf(false) }
    val carrito = cartViewModel.carrito
    var busqueda by remember { mutableStateOf("") }

    // 🔹 Cargar joyas desde Firestore
    LaunchedEffect(Unit) {
        db.collection("joyas")
            .get()
            .addOnSuccessListener { result ->
                joyas = result.map { it.toObject(Joya::class.java) }
                cargando = false
            }
            .addOnFailureListener {
                cargando = false
                // Aquí podrías mostrar un mensaje de error si la carga falla
            }
    }

    // 🔍 Filtro de búsqueda dinámico
    val joyasFiltradas = joyas.filter {
        it.nombre.contains(busqueda, ignoreCase = true) ||
                it.descripcion.contains(busqueda, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Catálogo de Joyas 💍") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },

        // 🔸 Botones flotantes (Mapa 📍, Historial 🧾 y Carrito 🛒)
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 📍 Botón del mapa (FAB secundario)
                SmallFloatingActionButton(
                    onClick = { navController.navigate("map") },
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(
                        Icons.Filled.LocationOn, // Usando Iconos estándar de Material
                        contentDescription = "Ubicación tienda",
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                }

                // 🧾 Botón del historial (FAB secundario)
                SmallFloatingActionButton(
                    onClick = { navController.navigate("historial") },
                    containerColor = MaterialTheme.colorScheme.tertiary
                ) {
                    Icon(
                        Icons.Filled.History, // Usando Iconos estándar de Material
                        contentDescription = "Historial de compras",
                        tint = MaterialTheme.colorScheme.onTertiary
                    )
                }

                // 🛒 Botón principal del carrito (FAB con Notificación/Badge)
                BadgedBox(
                    badge = {
                        if (carrito.isNotEmpty()) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError,
                                // Posicionar la insignia correctamente
                                modifier = Modifier.offset(x = (-6).dp, y = (6).dp)
                            ) {
                                Text("${carrito.size}")
                            }
                        }
                    }
                ) {
                    FloatingActionButton(
                        onClick = { mostrarCarrito = true },
                        containerColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(72.dp) // Hacemos el carrito más grande
                    ) {
                        Icon(
                            Icons.Filled.ShoppingCart, // Usando Iconos estándar de Material
                            contentDescription = "Carrito",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->

        if (cargando) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(modifier = Modifier.padding(padding)) {

                // 🔍 Campo de búsqueda
                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { busqueda = it },
                    label = { Text("Buscar joya...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    singleLine = true,
                    leadingIcon = {
                        // Usando Icons.Filled.Search para evitar depender de recursos locales
                        Icon(Icons.Filled.Search, contentDescription = "Buscar")
                    }
                )

                // 🔹 Catálogo filtrado
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxHeight() // Esto ayuda a que el Grid use el espacio restante
                ) {
                    items(joyasFiltradas) { joya ->
                        JoyaCard(joya = joya, onClick = { onJoyaClick(joya) })
                    }
                }
            }
        }

        // 🔹 Diálogo del carrito
        if (mostrarCarrito) {
            CartDialog(
                carrito = carrito,
                cartViewModel = cartViewModel,
                onDismiss = { mostrarCarrito = false },
                onNavigateToPayment = {
                    mostrarCarrito = false
                    navController.navigate("payment")
                }
            )
        }
    }
}


// Componente JoyaCard extraído para mejor legibilidad
@Composable
fun JoyaCard(joya: Joya, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = joya.imagen,
                contentDescription = joya.nombre,
                modifier = Modifier
                    .height(130.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(joya.nombre, fontWeight = FontWeight.Bold)
            Text("${joya.precio}", color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// Componente Diálogo del carrito extraído para mejor legibilidad
@Composable
fun CartDialog(
    carrito: List<Joya>,
    cartViewModel: CartViewModel,
    onDismiss: () -> Unit,
    onNavigateToPayment: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .width(300.dp)
                .heightIn(min = 200.dp)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🛍️ Carrito de compras", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))

                if (carrito.isEmpty()) {
                    Text("Tu carrito está vacío 😅")
                } else {
                    LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                        items(carrito.size) { index ->
                            val item = carrito[index]
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(item.nombre, maxLines = 1)
                                Text("S/${item.precio}", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Total: S/${"%.2f".format(cartViewModel.total())}",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = { cartViewModel.vaciar() }) {
                        Text("Vaciar")
                    }
                    Button(onClick = onDismiss) {
                        Text("Cerrar")
                    }
                }

                if (carrito.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onNavigateToPayment,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("💳 Comprar ahora")
                    }
                }
            }
        }
    }
}

// Nota: Asegúrate de que las clases 'Joya' y 'CartViewModel' estén definidas en tu proyecto.