package com.example.prestaaula.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.prestaaula.model.Rol

/**
 * Tabla "usuarios": el personal del colegio que usa la app.
 * roles: nombres de Rol separados por coma (ej: "UTP,DOCENTE" para quien tiene 2 roles).
 */
@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val correo: String,
    val rut: String,
    val roles: String
)

/** Convierte el texto "UTP,DOCENTE" en la lista de roles. */
fun UsuarioEntity.listaRoles(): List<Rol> =
    roles.split(",").mapNotNull { nombre -> Rol.entries.firstOrNull { it.name == nombre.trim() } }
