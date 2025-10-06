package com.tuempresa.inventario

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel // ✅ Este import es esencial

class CartViewModel : ViewModel() {

    val carrito = mutableStateListOf<Joya>()

    fun agregar(joya: Joya) {
        // Evita duplicados
        if (!carrito.contains(joya)) {
            carrito.add(joya)
        }
    }

    fun vaciar() {
        carrito.clear()
    }

    fun total(): Double {
        // Elimina el prefijo "S/" antes de convertir
        return carrito.sumOf {
            it.precio.replace("S/", "").trim().toDoubleOrNull() ?: 0.0
        }
    }
}
