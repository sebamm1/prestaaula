package com.example.prestaaula.model

/** Estados de un equipo del inventario (se guardan como texto en Room). */
object EstadoEquipo {
    const val DISPONIBLE = "Disponible"
    const val PRESTADO = "Prestado"
    const val MANTENCION = "En mantención"
}

/**
 * Estados de una reserva:
 * Pendiente (por aprobar) → Aprobada (por retirar) → Retirada (en uso) → Devuelta.
 * O bien Rechazada (el bloque queda libre).
 */
object EstadoReserva {
    const val PENDIENTE = "Pendiente"
    const val APROBADA = "Aprobada"
    const val RETIRADA = "Retirada"
    const val DEVUELTA = "Devuelta"
    const val RECHAZADA = "Rechazada"
}

/** Estado en que vuelve un equipo al registrar la devolución. */
object EstadoDevolucion {
    const val OK = "OK"
    const val CON_DANO = "Con daño"
    const val INCOMPLETO = "Incompleto"
    val TODOS = listOf(OK, CON_DANO, INCOMPLETO)
}

/** Tipos de equipo (con la opción "Otro", corrección de la v3). */
val TIPOS_EQUIPO = listOf("Notebook", "Proyector", "Tablet", "Cable", "Otro")

/** Bloques horarios del colegio. */
data class Bloque(val numero: Int, val horario: String)

val BLOQUES = listOf(
    Bloque(1, "08:00 a 08:45"),
    Bloque(2, "08:45 a 09:30"),
    Bloque(3, "09:45 a 10:30"),
    Bloque(4, "10:30 a 11:15"),
    Bloque(5, "11:30 a 12:15"),
    Bloque(6, "12:15 a 13:00"),
    Bloque(7, "14:00 a 14:45"),
    Bloque(8, "14:45 a 15:30")
)

fun horarioDe(bloque: Int): String = BLOQUES.firstOrNull { it.numero == bloque }?.horario ?: ""

/** Cursos de 1° a 8° básico, letras A, B y C. valor = lo que se guarda, etiqueta = lo que se ve. */
data class Curso(val valor: String, val etiqueta: String)

val CURSOS: List<Curso> = (1..8).flatMap { n ->
    listOf("A", "B", "C").map { letra -> Curso("$n°$letra", "$n° Básico $letra") }
}
