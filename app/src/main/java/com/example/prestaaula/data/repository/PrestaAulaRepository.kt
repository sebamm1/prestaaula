package com.example.prestaaula.data.repository

import android.content.Context
import androidx.room.withTransaction
import com.example.prestaaula.data.local.AppDatabase
import com.example.prestaaula.data.local.DatosPrueba
import com.example.prestaaula.data.local.entity.EquipoEntity
import com.example.prestaaula.data.local.entity.ReservaEntity
import com.example.prestaaula.data.local.entity.UsuarioEntity
import com.example.prestaaula.model.EstadoDevolucion
import com.example.prestaaula.model.EstadoEquipo
import com.example.prestaaula.model.EstadoReserva
import kotlinx.coroutines.flow.Flow

/**
 * Repository: capa intermedia entre los DAO y los ViewModel (clase "Database y uso completo").
 * Las operaciones que tocan dos tablas (reserva + equipo) van en una transacción,
 * para que el inventario y los préstamos queden siempre de acuerdo (caso, punto 3.4).
 */
class PrestaAulaRepository(private val db: AppDatabase) {

    private val usuarioDao = db.usuarioDao()
    private val equipoDao = db.equipoDao()
    private val reservaDao = db.reservaDao()

    // ---------- Lecturas que se actualizan solas (Flow) ----------
    val equipos: Flow<List<EquipoEntity>> = equipoDao.observarTodos()
    val reservas: Flow<List<ReservaEntity>> = reservaDao.observarTodas()

    // ---------- Datos de prueba ----------
    suspend fun asegurarDatosIniciales() {
        if (usuarioDao.contar() == 0) restablecerDatos()
    }

    suspend fun restablecerDatos() {
        db.withTransaction {
            reservaDao.eliminarTodas()
            equipoDao.eliminarTodos()
            usuarioDao.eliminarTodos()
            usuarioDao.insertarTodos(DatosPrueba.usuarios())
            equipoDao.insertarTodos(DatosPrueba.equipos())
            reservaDao.insertarTodas(DatosPrueba.reservas())
        }
    }

    // ---------- Usuarios ----------
    suspend fun buscarUsuarioPorCorreo(correo: String): UsuarioEntity? =
        usuarioDao.buscarPorCorreo(correo.trim())

    // ---------- Equipos ----------
    suspend fun obtenerEquipo(id: Int): EquipoEntity? = equipoDao.obtener(id)

    suspend fun existeEquipo(id: Int): Boolean = equipoDao.contarPorId(id) > 0

    /** Devuelve false si el N° interno ya existe. */
    suspend fun agregarEquipo(equipo: EquipoEntity): Boolean {
        if (existeEquipo(equipo.id)) return false
        equipoDao.insertar(equipo)
        return true
    }

    suspend fun marcarReparado(equipoId: Int) =
        equipoDao.actualizarEstado(equipoId, EstadoEquipo.DISPONIBLE)

    // ---------- Reservas ----------
    /** Crea la reserva "Pendiente". Devuelve false si el bloque ya estaba ocupado. */
    suspend fun crearReserva(
        equipoId: Int,
        docenteId: Int,
        docenteNombre: String,
        curso: String,
        fecha: String,
        bloque: Int
    ): Boolean = db.withTransaction {
        if (reservaDao.buscarChoque(equipoId, fecha, bloque) != null) {
            false
        } else {
            reservaDao.insertar(
                ReservaEntity(
                    equipoId = equipoId,
                    docenteId = docenteId,
                    docenteNombre = docenteNombre,
                    curso = curso,
                    fecha = fecha,
                    bloque = bloque,
                    estado = EstadoReserva.PENDIENTE
                )
            )
            true
        }
    }

    /** El Encargado(a) acepta o rechaza una reserva pendiente. */
    suspend fun responderReserva(reservaId: Int, aceptar: Boolean) {
        val reserva = reservaDao.obtener(reservaId) ?: return
        if (reserva.estado != EstadoReserva.PENDIENTE) return
        reservaDao.actualizar(
            reserva.copy(estado = if (aceptar) EstadoReserva.APROBADA else EstadoReserva.RECHAZADA)
        )
    }

    /** El docente confirma el retiro ("Ya lo retiré"): la reserva pasa a "En uso" y el equipo a "Prestado". */
    suspend fun confirmarRetiro(reservaId: Int, hora: String) {
        db.withTransaction {
            val reserva = reservaDao.obtener(reservaId) ?: return@withTransaction
            if (reserva.estado != EstadoReserva.APROBADA) return@withTransaction
            reservaDao.actualizar(reserva.copy(estado = EstadoReserva.RETIRADA, horaRetiro = hora))
            equipoDao.actualizarEstado(reserva.equipoId, EstadoEquipo.PRESTADO)
        }
    }

    /** El docente confirma que devolvió el equipo ("Ya lo devolví"). Falta la recepción del Encargado(a). */
    suspend fun confirmarDevolucionDocente(reservaId: Int, hora: String) {
        val reserva = reservaDao.obtener(reservaId) ?: return
        if (reserva.estado != EstadoReserva.RETIRADA || reserva.horaDevDocente != null) return
        reservaDao.actualizar(reserva.copy(horaDevDocente = hora))
    }

    /**
     * El Encargado(a) registra la devolución con el estado del equipo.
     * OK → el equipo queda Disponible. Con daño o Incompleto → En mantención.
     */
    suspend fun registrarDevolucion(
        reservaId: Int,
        estadoDevolucion: String,
        observaciones: String,
        fotoRuta: String?,
        hora: String
    ) {
        db.withTransaction {
            val reserva = reservaDao.obtener(reservaId) ?: return@withTransaction
            if (reserva.estado != EstadoReserva.RETIRADA) return@withTransaction
            reservaDao.actualizar(
                reserva.copy(
                    estado = EstadoReserva.DEVUELTA,
                    horaDevolucion = hora,
                    estadoDevolucion = estadoDevolucion,
                    observaciones = observaciones,
                    fotoDanoRuta = fotoRuta
                )
            )
            val nuevoEstado =
                if (estadoDevolucion == EstadoDevolucion.OK) EstadoEquipo.DISPONIBLE else EstadoEquipo.MANTENCION
            equipoDao.actualizarEstado(reserva.equipoId, nuevoEstado)
        }
    }

    companion object {
        @Volatile
        private var instancia: PrestaAulaRepository? = null

        fun obtener(context: Context): PrestaAulaRepository =
            instancia ?: synchronized(this) {
                instancia ?: PrestaAulaRepository(AppDatabase.obtenerInstancia(context)).also { instancia = it }
            }
    }
}
