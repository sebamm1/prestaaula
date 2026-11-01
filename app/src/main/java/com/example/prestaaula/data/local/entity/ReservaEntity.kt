package com.example.prestaaula.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tabla "reservas": cada reserva y su préstamo (caso, punto 3.2):
 * equipo, docente responsable, curso, fecha, hora de retiro y de devolución y observaciones.
 *
 * - fecha: texto ISO "yyyy-MM-dd".
 * - estado: ver EstadoReserva (Pendiente, Aprobada, Retirada, Devuelta, Rechazada).
 * - horaDevDocente: hora en que el docente confirmó que lo devolvió ("Ya lo devolví").
 * - horaDevolucion: hora en que el Encargado(a) registró la devolución.
 * - estadoDevolucion: OK, Con daño o Incompleto.
 */
@Entity(tableName = "reservas")
data class ReservaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val equipoId: Int,
    val docenteId: Int,
    val docenteNombre: String,
    val curso: String,
    val fecha: String,
    val bloque: Int,
    val estado: String,
    val horaRetiro: String? = null,
    val horaDevDocente: String? = null,
    val horaDevolucion: String? = null,
    val estadoDevolucion: String? = null,
    val observaciones: String? = null,
    val fotoDanoRuta: String? = null
)
