package com.example.prestaaula.viewmodel

import com.example.prestaaula.data.local.entity.EquipoEntity
import com.example.prestaaula.data.local.entity.ReservaEntity

/** Una reserva junto con su equipo (para mostrar el nombre y el tipo en pantalla). */
data class ReservaItem(val reserva: ReservaEntity, val equipo: EquipoEntity?) {
    val nombreEquipo: String get() = equipo?.nombre ?: "Equipo #${reserva.equipoId}"
}

/** Une cada reserva con su equipo. */
fun List<ReservaEntity>.conEquipos(equipos: List<EquipoEntity>): List<ReservaItem> {
    val porId = equipos.associateBy { it.id }
    return map { ReservaItem(it, porId[it.equipoId]) }
}

/** Orden de más reciente a más antigua (fecha y luego bloque). */
val ORDEN_RECIENTES: Comparator<ReservaEntity> =
    compareByDescending<ReservaEntity> { it.fecha }.thenByDescending { it.bloque }

/** Orden de más próxima a más lejana. */
val ORDEN_PROXIMAS: Comparator<ReservaEntity> =
    compareBy<ReservaEntity> { it.fecha }.thenBy { it.bloque }
