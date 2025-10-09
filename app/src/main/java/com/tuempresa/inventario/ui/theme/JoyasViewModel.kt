package com.tuempresa.inventario

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class JoyasViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    val joyas = mutableStateListOf<Joya>()
    var cargando = mutableStateOf(true)

    fun cargarJoyas() {
        viewModelScope.launch {
            db.collection("joyas")
                .get()
                .addOnSuccessListener { result ->
                    joyas.clear()
                    for (document in result) {
                        val joya = document.toObject(Joya::class.java).copy(id = document.id)
                        joyas.add(joya)
                    }
                    cargando.value = false
                }
                .addOnFailureListener {
                    cargando.value = false
                }
        }
    }
}
