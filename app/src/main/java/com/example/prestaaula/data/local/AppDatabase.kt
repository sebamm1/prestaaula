package com.example.prestaaula.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.prestaaula.data.local.dao.EquipoDao
import com.example.prestaaula.data.local.dao.ReservaDao
import com.example.prestaaula.data.local.dao.UsuarioDao
import com.example.prestaaula.data.local.entity.EquipoEntity
import com.example.prestaaula.data.local.entity.ReservaEntity
import com.example.prestaaula.data.local.entity.UsuarioEntity

/**
 * Base de datos local (Room / SQLite). Igual que en la clase "Database y uso completo":
 * se construye UNA sola vez (Singleton).
 */
@Database(
    entities = [UsuarioEntity::class, EquipoEntity::class, ReservaEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun equipoDao(): EquipoDao
    abstract fun reservaDao(): ReservaDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun obtenerInstancia(context: Context): AppDatabase {
            return instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "prestaaula_db"
                ).build().also { instancia = it }
            }
        }
    }
}
