package com.example.dumitrunascoproyecto.inicioSesion

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.dumitrunascoproyecto.admin.AdminActivity
import com.example.dumitrunascoproyecto.database.AppDatabase
import com.example.dumitrunascoproyecto.database.RegisterViewModel
import com.example.dumitrunascoproyecto.database.UniversalViewModelFactory
import com.example.dumitrunascoproyecto.databinding.ActivityInicioSesionBinding
import com.example.dumitrunascoproyecto.paginaInicio.PaginaInicio
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
class InicioSesion : AppCompatActivity() {

    // Declaración del binding
    private lateinit var binding: ActivityInicioSesionBinding

    // Declaraciones de elementos de la interfaz de usuario
    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var botonOculto: Button

    // Declaración del ViewModel
    private lateinit var viewModel: RegisterViewModel




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioSesionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inicializarUI()
        inicializarViewModel()
        configurarEventos()
    }

    // Inicialización de los elementos de la interfaz de usuario
    private fun inicializarUI() {
        editTextUsername = binding.usernameLogin
        editTextPassword = binding.passwordLogin
        buttonLogin = binding.buttonLogin
        botonOculto = binding.buttonOculta
    }

    // Inicialización del ViewModel
    private fun inicializarViewModel() {
        val factory = UniversalViewModelFactory {
            RegisterViewModel(AppDatabase.getDatabase(this))
        }
        viewModel = ViewModelProvider(this, factory)[RegisterViewModel::class.java]
    }


    private fun configurarEventos() {
        buttonLogin.setOnClickListener { iniciarSesion() }

        botonOculto.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Iniciar el proceso de administrador en una nueva coroutine después de 3 segundos
                    lifecycleScope.launch {
                        kotlinx.coroutines.delay(3000)
                        val intent = Intent(this@InicioSesion, AdminActivity::class.java)
                        startActivity(intent)
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    // Cancelar el proceso si se levanta el dedo antes de 3 segundos
                    lifecycleScope.coroutineContext.cancelChildren()
                    true
                }
                else -> false
            }
        }
    }


    // Método para manejar el inicio de sesión
    private fun iniciarSesion() {
        val username = editTextUsername.text.toString().trim()
        val password = editTextPassword.text.toString()

        if (username.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Por favor, ingresa tanto el nombre de usuario como la contraseña", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.iniciarSesion(username, password) { user ->
            if (user != null) {
                val intent = if (user.isAdmin) {
                    Intent(this@InicioSesion, AdminActivity::class.java)
                } else {
                    Intent(this@InicioSesion, PaginaInicio::class.java).apply {
                        putExtra("username", user.username)
                        putExtra("photo", user.photo)
                        putExtra("fechaNacimiento", user.fechaNacimiento)
                        putExtra("email", user.email)
                    }
                }
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@InicioSesion, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }
    }

}