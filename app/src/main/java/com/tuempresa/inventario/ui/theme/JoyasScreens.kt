package com.tuempresa.inventario

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import kotlinx.coroutines.delay

// Modelo de datos
data class Joya(
    val id: Int,
    val nombre: String,
    val precio: String,
    val descripcion: String,
    val imagen: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoyasCatalogScreen(
    onJoyaClick: (Joya) -> Unit,
    cartViewModel: CartViewModel,
    navController: NavController
) {
    val joyas = listOf(
        Joya(1, "Anillo de Plata", "S/120", "Anillo de plata 925 con zirconia.", R.drawable.anillo),
        Joya(2, "Collar de Oro", "S/350", "Collar de oro 18k con dije de cruz.", R.drawable.collargold),
        Joya(3, "Aretes de Perla", "S/90", "Aretes con perlas naturales.", R.drawable.aretes),
        Joya(4, "Pulsera ", "S/150", "Pulsera de acero quirúrgico plateada, que se le pueden agregar CHARMS.", R.drawable.pulsera)
    )

    val carrito = cartViewModel.carrito
    var mostrarCarrito by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Catálogo de Joyas 💍") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },

        // 🔸 Ambos botones flotantes (Mapa 📍 + Carrito 🛒)
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 4.dp, bottom = 4.dp)
            ) {
                // 📍 Botón del mapa
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable { navController.navigate("map") },
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondary,
                        shadowElevation = 6.dp
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_location),
                            contentDescription = "Ubicación tienda",
                            tint = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxSize()
                        )
                    }
                }

                // 🛒 Botón del carrito
                BadgedBox(
                    badge = {
                        if (carrito.isNotEmpty()) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError,
                                modifier = Modifier.offset(x = (-6).dp, y = (6).dp)
                            ) {
                                Text("${carrito.size}")
                            }
                        }
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable { mostrarCarrito = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            shadowElevation = 6.dp
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_cart),
                                contentDescription = "Carrito",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .fillMaxSize()
                            )
                        }
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(12.dp)
        ) {
            items(joyas) { joya ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onJoyaClick(joya) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = joya.imagen),
                            contentDescription = joya.nombre,
                            modifier = Modifier
                                .height(130.dp)
                                .fillMaxWidth(),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(joya.nombre, fontWeight = FontWeight.Bold)
                        Text(joya.precio, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        if (mostrarCarrito) {
            Dialog(onDismissRequest = { mostrarCarrito = false }) {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    tonalElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🛍️ Carrito de compras", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        if (carrito.isEmpty()) {
                            Text("Tu carrito está vacío 😅")
                        } else {
                            LazyColumn {
                                items(carrito.size) { index ->
                                    val item = carrito[index]
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(item.nombre)
                                        Text(item.precio, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Total: S/${"%.2f".format(cartViewModel.total())}",
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(onClick = { cartViewModel.vaciar() }) {
                                Text("Vaciar")
                            }
                            Button(onClick = { mostrarCarrito = false }) {
                                Text("Cerrar")
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoyaDetailScreen(joya: Joya, onBack: () -> Unit, cartViewModel: CartViewModel) {
    var agregado by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf(false) }

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = joya.imagen),
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
            }

            if (showSnackbar) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
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
}
