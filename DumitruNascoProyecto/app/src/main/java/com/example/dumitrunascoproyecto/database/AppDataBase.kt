package com.example.dumitrunascoproyecto.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dumitrunascoproyecto.registrarse.Registro

// Anotación para definir una base de datos de Room y sus entidades
@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    // Método abstracto para obtener el DAO de la aplicación
    abstract fun appDao(): AppDao

    companion object {
        // Marcador 'Volatile' para asegurar que la instancia sea consistente en todos los hilos
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Método para obtener la instancia de la base de datos
        fun getDatabase(context: Context): AppDatabase {
            // Retorna la instancia existente si ya ha sido creada
            return INSTANCE ?: synchronized(this) {
                // Crea una nueva instancia si es necesario
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "FoodDatabase" // Nombre de la base de datos
                )
                    // Estrategia de migración para lidiar con esquemas de base de datos actualizados
                    .fallbackToDestructiveMigration()
                    .build()

                // Asigna la nueva instancia a INSTANCE
                INSTANCE = instance
                instance // Retorna la nueva instancia
            }
        }



    }
}
