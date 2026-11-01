package com.example.prestaaula.data.local

import com.example.prestaaula.data.local.entity.EquipoEntity
import com.example.prestaaula.data.local.entity.ReservaEntity
import com.example.prestaaula.data.local.entity.UsuarioEntity
import com.example.prestaaula.model.EstadoDevolucion
import com.example.prestaaula.model.EstadoEquipo
import com.example.prestaaula.model.EstadoReserva
import com.example.prestaaula.util.Fechas
import java.time.LocalDate

/**
 * Datos de prueba: los mismos de la maqueta v6.
 * Las fechas se calculan a partir de HOY (días hábiles), para que siempre se vean actuales.
 * Se cargan la primera vez y con "Restablecer datos de prueba".
 */
object DatosPrueba {

    const val MARIA = 1
    const val PEDRO = 2
    const val CARLA = 3
    const val ANDREA = 4

    fun usuarios(): List<UsuarioEntity> = listOf(
        UsuarioEntity(MARIA, "María Soto", "maria.soto@colegio.cl", "12.345.678-5", "DOCENTE"),
        UsuarioEntity(PEDRO, "Pedro Rojas", "pedro.rojas@colegio.cl", "11.111.111-1", "DOCENTE"),
        UsuarioEntity(CARLA, "Carla Vega", "carla.vega@colegio.cl", "22.222.222-2", "ENCARGADO"),
        UsuarioEntity(ANDREA, "Andrea Pino", "andrea.pino@colegio.cl", "1.234.567-4", "UTP,DOCENTE")
    )

    fun equipos(): List<EquipoEntity> = listOf(
        EquipoEntity(1, "Notebook", "Lenovo", "Notebook Lenovo 01", EstadoEquipo.DISPONIBLE),
        EquipoEntity(2, "Notebook", "HP", "Notebook HP 02", EstadoEquipo.PRESTADO),
        EquipoEntity(3, "Proyector", "Epson", "Proyector Epson 01", EstadoEquipo.DISPONIBLE),
        EquipoEntity(4, "Proyector", "Epson", "Proyector Epson 02", EstadoEquipo.MANTENCION),
        EquipoEntity(5, "Tablet", "Samsung", "Tablet Samsung 01", EstadoEquipo.DISPONIBLE),
        EquipoEntity(6, "Cable", "HDMI", "Cable HDMI 01", EstadoEquipo.PRESTADO)
    )

    fun reservas(hoy: LocalDate = Fechas.hoy()): List<ReservaEntity> {
        val h = hoy.toString()
        fun dia(n: Int) = Fechas.habil(hoy, n).toString()
        return listOf(
            ReservaEntity(equipoId = 3, docenteId = PEDRO, docenteNombre = "Pedro Rojas", curso = "7°A", fecha = h, bloque = 3,
                estado = EstadoReserva.APROBADA),
            ReservaEntity(equipoId = 1, docenteId = MARIA, docenteNombre = "María Soto", curso = "8°B", fecha = h, bloque = 5,
                estado = EstadoReserva.APROBADA),
            ReservaEntity(equipoId = 2, docenteId = PEDRO, docenteNombre = "Pedro Rojas", curso = "7°B", fecha = h, bloque = 2,
                estado = EstadoReserva.RETIRADA, horaRetiro = "08:50", horaDevDocente = "09:32"),
            ReservaEntity(equipoId = 6, docenteId = MARIA, docenteNombre = "María Soto", curso = "8°A", fecha = h, bloque = 4,
                estado = EstadoReserva.RETIRADA, horaRetiro = "10:35"),
            ReservaEntity(equipoId = 5, docenteId = MARIA, docenteNombre = "María Soto", curso = "6°A", fecha = dia(-3), bloque = 1,
                estado = EstadoReserva.DEVUELTA, horaRetiro = "08:05", horaDevolucion = "08:40",
                estadoDevolucion = EstadoDevolucion.OK, observaciones = ""),
            ReservaEntity(equipoId = 3, docenteId = MARIA, docenteNombre = "María Soto", curso = "5°B", fecha = dia(1), bloque = 2,
                estado = EstadoReserva.PENDIENTE),
            ReservaEntity(equipoId = 1, docenteId = PEDRO, docenteNombre = "Pedro Rojas", curso = "4°A", fecha = dia(1), bloque = 6,
                estado = EstadoReserva.PENDIENTE),
            ReservaEntity(equipoId = 4, docenteId = PEDRO, docenteNombre = "Pedro Rojas", curso = "3°C", fecha = dia(-2), bloque = 3,
                estado = EstadoReserva.DEVUELTA, horaRetiro = "09:50", horaDevolucion = "10:30",
                estadoDevolucion = EstadoDevolucion.INCOMPLETO, observaciones = "Falta cable de poder"),
            ReservaEntity(equipoId = 2, docenteId = ANDREA, docenteNombre = "Andrea Pino", curso = "2°A", fecha = dia(-4), bloque = 1,
                estado = EstadoReserva.DEVUELTA, horaRetiro = "08:02", horaDevolucion = "08:44",
                estadoDevolucion = EstadoDevolucion.CON_DANO, observaciones = "Pantalla trizada"),
            ReservaEntity(equipoId = 5, docenteId = PEDRO, docenteNombre = "Pedro Rojas", curso = "1°B", fecha = dia(-1), bloque = 7,
                estado = EstadoReserva.RECHAZADA),
            ReservaEntity(equipoId = 3, docenteId = PEDRO, docenteNombre = "Pedro Rojas", curso = "6°B", fecha = dia(-2), bloque = 5,
                estado = EstadoReserva.DEVUELTA, horaRetiro = "11:32", horaDevolucion = "12:14",
                estadoDevolucion = EstadoDevolucion.OK, observaciones = ""),
            ReservaEntity(equipoId = 2, docenteId = MARIA, docenteNombre = "María Soto", curso = "4°C", fecha = dia(-6), bloque = 4,
                estado = EstadoReserva.DEVUELTA, horaRetiro = "10:31", horaDevolucion = "11:16",
                estadoDevolucion = EstadoDevolucion.INCOMPLETO, observaciones = "Falta el cargador")
        )
    }
}
