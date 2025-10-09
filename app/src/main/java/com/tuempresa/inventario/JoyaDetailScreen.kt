package com.tuempresa.inventario

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import kotlinx.coroutines.delay

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
