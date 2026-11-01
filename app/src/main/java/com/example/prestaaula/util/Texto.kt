package com.example.prestaaula.util

import java.text.Normalizer

/**
 * Búsqueda tolerante (corrección #4 del profesor):
 * ignora mayúsculas, tildes y el símbolo °.
 * Ej: "maria" encuentra "María Soto" y "8a" encuentra "8°A".
 */
object Texto {
    private val MARCAS = Regex("\\p{Mn}+")

    fun normalizar(texto: String): String =
        Normalizer.normalize(texto.lowercase(), Normalizer.Form.NFD)
            .replace(MARCAS, "")
            .replace("°", "")
            .replace("º", "")
            .trim()

    /** true si la búsqueda está vacía o si el texto la contiene. */
    fun coincide(texto: String, busqueda: String): Boolean {
        val q = normalizar(busqueda)
        return q.isEmpty() || normalizar(texto).contains(q)
    }
}
