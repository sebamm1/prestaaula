package com.example.prestaaula.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.prestaaula.data.local.entity.EquipoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipoDao {
    /** Flow: la pantalla se actualiza sola cuando cambia un equipo. */
    @Query("SELECT * FROM equipos ORDER BY id ASC")
    fun observarTodos(): Flow<List<EquipoEntity>>

    @Query("SELECT * FROM equipos WHERE id = :id LIMIT 1")
    suspend fun obtener(id: Int): EquipoEntity?

    @Insert
    suspend fun insertar(equipo: EquipoEntity)

    @Insert
    suspend fun insertarTodos(equipos: List<EquipoEntity>)

    @Query("UPDATE equipos SET estado = :estado WHERE id = :id")
    suspend fun actualizarEstado(id: Int, estado: String)

    @Query("SELECT COUNT(*) FROM equipos WHERE id = :id")
    suspend fun contarPorId(id: Int): Int

    @Query("DELETE FROM equipos")
    suspend fun eliminarTodos()
}
