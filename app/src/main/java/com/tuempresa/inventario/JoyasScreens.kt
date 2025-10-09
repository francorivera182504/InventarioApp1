package com.tuempresa.inventario
import androidx.compose.ui.res.painterResource

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore

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
    var busqueda by remember { mutableStateOf("") } // 🔍 campo de búsqueda

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

        // 🔸 Botones flotantes (Mapa 📍 + Carrito 🛒)
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
            ) {
                // 📍 Botón del mapa
                FloatingActionButton(
                    onClick = { navController.navigate("map") },
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_location),
                        contentDescription = "Ubicación tienda",
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
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
                    FloatingActionButton(
                        onClick = { mostrarCarrito = true },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_cart),
                            contentDescription = "Carrito",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->

        if (cargando) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = "Buscar"
                        )
                    }
                )

                // 🔹 Catálogo filtrado
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    items(joyasFiltradas) { joya ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onJoyaClick(joya) },
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
                                Text(joya.precio, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }

        // 🔹 Diálogo del carrito
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

