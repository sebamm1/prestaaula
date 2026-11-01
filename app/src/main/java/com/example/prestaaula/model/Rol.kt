package com.example.prestaaula.model

/**
 * Roles de la app según el caso PrestaAula.
 * El rol lo trae cada usuario (no se elige a mano), y define:
 *  - qué pantallas puede ver,
 *  - el color de acento del tema (corrección #11 del profesor).
 */
enum class Rol(val etiqueta: String) {
    DOCENTE("Docente"),
    ENCARGADO("Encargado(a) CRA"),
    UTP("UTP / Administración")
}
