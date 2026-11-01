package com.example.prestaaula.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tabla "equipos": el inventario del CRA.
 * Campos que pide el caso (3.2): tipo, marca, número de identificación interno y estado.
 * - id: el N° de identificación interno (lo escribe el Encargado(a) al agregar el equipo).
 * - nombre: el texto que se muestra, ej "Notebook Lenovo 01".
 * - fotoRuta: ruta de la foto del equipo guardada en la app (opcional).
 */
@Entity(tableName = "equipos")
data class EquipoEntity(
    @PrimaryKey val id: Int,
    val tipo: String,
    val marca: String,
    val nombre: String,
    val estado: String,
    val fotoRuta: String? = null
)
