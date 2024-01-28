package com.example.dumitrunascoproyecto.database


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "users")
data class User(
    @PrimaryKey val username: String,
    val email: String,
    val passwordHash: String,
    val fechaNacimiento: String,
    val photo: ByteArray? = null,
    val isAdmin: Boolean = false // Campo nuevo para determinar si es administrador
):Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (username != other.username) return false
        if (email != other.email) return false
        if (passwordHash != other.passwordHash) return false
        if (fechaNacimiento != other.fechaNacimiento) return false
        if (photo != null) {
            if (other.photo == null) return false
            if (!photo.contentEquals(other.photo)) return false
        } else if (other.photo != null) return false
        if (isAdmin != other.isAdmin) return false

        return true
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + passwordHash.hashCode()
        result = 31 * result + fechaNacimiento.hashCode()
        result = 31 * result + (photo?.contentHashCode() ?: 0)
        result = 31 * result + isAdmin.hashCode()
        return result
    }
}

