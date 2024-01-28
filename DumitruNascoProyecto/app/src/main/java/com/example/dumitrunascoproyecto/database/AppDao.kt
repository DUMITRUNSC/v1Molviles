package com.example.dumitrunascoproyecto.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AppDao {

    @Insert
    fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE username = :username")
    fun findUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE email = :email")
    fun findUserByEmail(email: String): User?

    @Query("SELECT * FROM users")
    fun getAllUsers(): List<User>

    @Delete
    suspend fun deleteUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE users SET passwordHash = :newPassword WHERE username = :username")
    suspend fun updatePassword(username: String, newPassword: String)


    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()

}
