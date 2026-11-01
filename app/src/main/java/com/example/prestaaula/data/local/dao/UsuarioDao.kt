package com.example.prestaaula.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.prestaaula.data.local.entity.UsuarioEntity

@Dao
interface UsuarioDao {
    @Insert
    suspend fun insertarTodos(usuarios: List<UsuarioEntity>)

    @Query("SELECT * FROM usuarios WHERE lower(correo) = lower(:correo) LIMIT 1")
    suspend fun buscarPorCorreo(correo: String): UsuarioEntity?

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int

    @Query("DELETE FROM usuarios")
    suspend fun eliminarTodos()
}
