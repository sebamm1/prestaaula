package com.example.prestaaula.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.prestaaula.data.local.entity.ReservaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservaDao {
    @Query("SELECT * FROM reservas ORDER BY fecha DESC, bloque DESC")
    fun observarTodas(): Flow<List<ReservaEntity>>

    @Query("SELECT * FROM reservas WHERE id = :id LIMIT 1")
    suspend fun obtener(id: Int): ReservaEntity?

    /** Choque de horario: misma reserva de equipo, fecha y bloque (las rechazadas no cuentan). */
    @Query(
        "SELECT * FROM reservas WHERE equipoId = :equipoId AND fecha = :fecha AND bloque = :bloque " +
            "AND estado != 'Rechazada' LIMIT 1"
    )
    suspend fun buscarChoque(equipoId: Int, fecha: String, bloque: Int): ReservaEntity?

    @Insert
    suspend fun insertar(reserva: ReservaEntity): Long

    @Insert
    suspend fun insertarTodas(reservas: List<ReservaEntity>)

    @Update
    suspend fun actualizar(reserva: ReservaEntity)

    @Query("DELETE FROM reservas")
    suspend fun eliminarTodas()
}
