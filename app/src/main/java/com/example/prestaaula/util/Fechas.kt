package com.example.prestaaula.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/** Utilidades de fechas y horas (java.time, disponible desde Android 8.0 = minSdk 26). */
object Fechas {
    private val LOCALE_CL: Locale = Locale.forLanguageTag("es-CL")
    private val FORMATO_FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    private val FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm")

    fun hoy(): LocalDate = LocalDate.now()

    /** Hora actual "HH:mm" (para retiro y devolución). */
    fun horaActual(): String = LocalTime.now().format(FORMATO_HORA)

    fun esFinDeSemana(fecha: LocalDate): Boolean =
        fecha.dayOfWeek == DayOfWeek.SATURDAY || fecha.dayOfWeek == DayOfWeek.SUNDAY

    /** Día hábil a "n" días de distancia (negativo = hacia atrás). Se usa para los datos de prueba. */
    fun habil(desde: LocalDate, n: Int): LocalDate {
        var fecha = desde
        var contados = 0
        val paso = if (n < 0) -1L else 1L
        while (contados < kotlin.math.abs(n)) {
            fecha = fecha.plusDays(paso)
            if (!esFinDeSemana(fecha)) contados++
        }
        return fecha
    }

    /**
     * Primer día que se puede reservar: hoy si es hábil, si no el siguiente hábil.
     * Si el equipo está prestado ahora, desde mañana.
     */
    fun primerDiaDisponible(equipoPrestado: Boolean): LocalDate {
        var fecha = hoy()
        if (equipoPrestado) fecha = fecha.plusDays(1)
        while (esFinDeSemana(fecha)) fecha = fecha.plusDays(1)
        return fecha
    }

    /** "Hoy" o "dd-MM-yyyy" a partir de una fecha guardada como texto ISO (yyyy-MM-dd). */
    fun texto(fechaIso: String): String {
        val fecha = runCatching { LocalDate.parse(fechaIso) }.getOrNull() ?: return fechaIso
        return if (fecha == hoy()) "Hoy" else fecha.format(FORMATO_FECHA)
    }

    /** "Noviembre de 2026" */
    fun nombreMes(mes: YearMonth): String {
        val nombre = mes.month.getDisplayName(TextStyle.FULL, LOCALE_CL)
        return nombre.replaceFirstChar { it.titlecase(LOCALE_CL) } + " de " + mes.year
    }
}
