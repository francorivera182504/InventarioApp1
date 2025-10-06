package com.tuempresa.inventario

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(onBack: () -> Unit) {

    val tiendaUbicacion = LatLng(-7.157829, -78.518968)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(tiendaUbicacion, 15f)
    }

    @Composable
    fun SimpleTopBar(title: String, onBack: () -> Unit) {
        Surface(
            tonalElevation = 4.dp,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, style = MaterialTheme.typography.titleLarge)
            }
        }
    }

    Scaffold(
        topBar = { SimpleTopBar(title = "Ubicación de la tienda", onBack = onBack) },
        bottomBar = {
            // 🔙 BOTÓN INFERIOR PARA VOLVER AL CATÁLOGO
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("⬅ Volver al Catálogo", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { innerPadding ->
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = rememberMarkerState(position = tiendaUbicacion),
                title = "Joyas SOLOPARAMI",
                snippet = "605 Jirón Cruz de Piedra"
            )
        }
    }
}
