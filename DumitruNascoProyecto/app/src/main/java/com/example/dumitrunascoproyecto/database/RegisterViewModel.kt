package com.example.dumitrunascoproyecto.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mindrot.jbcrypt.BCrypt

class RegisterViewModel(private val baseDeDatos: AppDatabase) : ViewModel() {

    // Guarda un usuario en la base de datos
    fun guardarUsuario(usuario: User) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                baseDeDatos.appDao().insertUser(usuario)
                withContext(Dispatchers.Main) {
                    _estadoActualizacion.postValue("Usuario guardado con éxito.")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _estadoActualizacion.postValue("Error al guardar usuario: ${e.message}")
                }
            }
        }
    }

    // Estado de actualización para comunicar al UI
    private val _estadoActualizacion = MutableLiveData<String>()
    val estadoActualizacion: LiveData<String>
        get() = _estadoActualizacion

    // Actualiza la contraseña de un usuario
    fun actualizarContrasena(nombreUsuario: String, contrasenaHash: String) {
        viewModelScope.launch {
            try {
                baseDeDatos.appDao().updatePassword(nombreUsuario, contrasenaHash)
                _estadoActualizacion.postValue("Contraseña actualizada con éxito.")
            } catch (e: Exception) {
                _estadoActualizacion.postValue("Error al actualizar la contraseña.")
            }
        }
    }

    // Inicia sesión verificando las credenciales del usuario
    fun iniciarSesion(nombreUsuario: String, contrasena: String, alResultado: (User?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val usuario = baseDeDatos.appDao().findUserByUsername(nombreUsuario)
                if (usuario != null) {
                    if (BCrypt.checkpw(contrasena, usuario.passwordHash)) {
                        withContext(Dispatchers.Main) { alResultado(usuario) }
                    } else {
                        withContext(Dispatchers.Main) { alResultado
                            (null) }
                    }
                } else {
                    withContext(Dispatchers.Main) { alResultado(null) }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { alResultado(null) }
            }
        }
    }

    // LiveData que contiene la lista de usuarios
    private val _usuarios = MutableLiveData<List<User>>()
    val usuarios: LiveData<List<User>> = _usuarios

    // Obtiene todos los usuarios de la base de datos
    fun obtenerTodosLosUsuarios() {
        viewModelScope.launch(Dispatchers.IO) {
            val listaDeUsuarios = baseDeDatos.appDao().getAllUsers()
            _usuarios.postValue(listaDeUsuarios)
        }
    }

    // Elimina un usuario de la base de datos
    fun eliminarUsuario(usuario: User) {
        viewModelScope.launch(Dispatchers.IO) {
            baseDeDatos.appDao().deleteUser(usuario)
            obtenerTodosLosUsuarios() // Actualizar la lista después de eliminar un usuario
        }
    }

    // Elimina todos los usuarios de la base de datos
    fun eliminarTodosLosUsuarios() {
        viewModelScope.launch(Dispatchers.IO) {
            baseDeDatos.appDao().deleteAllUsers()
            _usuarios.postValue(emptyList()) // Actualizar la lista a vacío
        }
    }
}