package com.example.prestaaula.validation

import java.time.LocalDate

/**
 * VALIDACIÓN CENTRALIZADA (rúbrica IE 2.2.1).
 * Funciones Kotlin puras: no tocan la interfaz ni Compose.
 * Cada función devuelve un mapa campo → mensaje de error (vacío = todo correcto).
 * Los ViewModel las llaman y la pantalla solo muestra el resultado.
 */
object Validador {

    // Nombres de los campos (claves del mapa de errores)
    const val NOMBRE = "nombre"
    const val CORREO = "correo"
    const val RUT = "rut"
    const val CURSO = "curso"
    const val FECHA = "fecha"
    const val BLOQUE = "bloque"
    const val TIPO = "tipo"
    const val MARCA = "marca"
    const val CODIGO = "codigo"
    const val ESTADO = "estado"
    const val PRESTAMO = "prestamo"

    private val REGEX_CORREO = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
    private val REGEX_RUT = Regex("^\\d{7,8}[\\dK]$")
    private val REGEX_NUMERO = Regex("^\\d+$")

    // ---------- Login ----------
    fun login(nombre: String, correo: String, rut: String): Map<String, String> {
        val errores = mutableMapOf<String, String>()
        if (nombre.trim().length < 3) errores[NOMBRE] = "Ingresa tu nombre completo"
        if (!REGEX_CORREO.matches(correo.trim())) errores[CORREO] = "Correo inválido (ej: docente@colegio.cl)"
        val limpio = limpiarRut(rut)
        if (!REGEX_RUT.matches(limpio)) errores[RUT] = "Formato inválido (ej: 12.345.678-5)"
        else if (!rutValido(rut)) errores[RUT] = "RUT inválido, revisa el dígito verificador"
        return errores
    }

    /** Quita puntos, guion y espacios: "12.345.678-5" → "123456785". */
    fun limpiarRut(rut: String): String = rut.replace(".", "").replace("-", "").replace(" ", "").uppercase()

    /** Dígito verificador con el algoritmo módulo 11. */
    fun rutValido(rut: String): Boolean {
        val s = limpiarRut(rut)
        if (!REGEX_RUT.matches(s)) return false
        val cuerpo = s.dropLast(1)
        val dv = s.last()
        var suma = 0
        var multiplicador = 2
        for (i in cuerpo.indices.reversed()) {
            suma += (cuerpo[i] - '0') * multiplicador
            multiplicador = if (multiplicador == 7) 2 else multiplicador + 1
        }
        val resto = 11 - suma % 11
        val esperado = when (resto) {
            11 -> '0'
            10 -> 'K'
            else -> ('0' + resto)
        }
        return esperado == dv
    }

    // ---------- Nueva reserva ----------
    /**
     * @param prestadoHoy true si el equipo está prestado ahora y la fecha elegida es hoy.
     * @param ocupadoPor nombre del docente que ya tiene ese bloque (null si está libre).
     */
    fun reserva(
        curso: String,
        fecha: LocalDate?,
        bloque: Int?,
        prestadoHoy: Boolean,
        ocupadoPor: String?
    ): Map<String, String> {
        val errores = mutableMapOf<String, String>()
        if (curso.isBlank()) errores[CURSO] = "Elige el curso"
        if (fecha == null) errores[FECHA] = "Elige un día en el calendario"
        else if (prestadoHoy) errores[FECHA] = "Hoy este equipo está prestado: elige otro día"
        if (bloque == null) errores[BLOQUE] = "Elige un horario libre"
        else if (ocupadoPor != null) errores[BLOQUE] = "Bloque ocupado por $ocupadoPor"
        return errores
    }

    // ---------- Agregar equipo ----------
    fun equipo(tipo: String, marca: String, codigo: String, estado: String, codigoEnUso: Boolean): Map<String, String> {
        val errores = mutableMapOf<String, String>()
        if (tipo.isBlank()) errores[TIPO] = "Elige el tipo de equipo"
        if (marca.trim().length < 2) errores[MARCA] = "Ingresa la marca"
        val c = codigo.trim()
        if (!REGEX_NUMERO.matches(c) || (c.toIntOrNull() ?: 0) < 1) errores[CODIGO] = "Ingresa un número entero mayor a 0"
        else if (codigoEnUso) errores[CODIGO] = "Ese número ya está en uso"
        if (estado.isBlank()) errores[ESTADO] = "Marca el estado inicial del equipo"
        return errores
    }

    // ---------- Registrar devolución (observaciones opcionales, corrección #5) ----------
    fun devolucion(prestamoId: Int?, estado: String): Map<String, String> {
        val errores = mutableMapOf<String, String>()
        if (prestamoId == null) errores[PRESTAMO] = "Elige qué préstamo devuelven"
        if (estado.isBlank()) errores[ESTADO] = "Marca el estado del equipo"
        return errores
    }
}
