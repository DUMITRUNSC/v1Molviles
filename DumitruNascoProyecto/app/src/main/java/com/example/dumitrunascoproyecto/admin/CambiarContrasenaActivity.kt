package com.example.dumitrunascoproyecto.admin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.dumitrunascoproyecto.database.AppDatabase
import com.example.dumitrunascoproyecto.database.RegisterViewModel
import com.example.dumitrunascoproyecto.database.UniversalViewModelFactory
import com.example.dumitrunascoproyecto.database.User
import com.example.dumitrunascoproyecto.databinding.ActivityRecicleRecuperarContraseniaBinding
import com.example.dumitrunascoproyecto.registrarse.PasswordCriteriaPopup
import com.example.dumitrunascoproyecto.validaciones.Validator
import kotlinx.coroutines.*
import org.mindrot.jbcrypt.BCrypt

class CambiarContrasenaActivity : AppCompatActivity() {

    private lateinit var campoNuevaContrasena: EditText
    private lateinit var campoConfirmarContrasena: EditText
    private lateinit var botonGuardar: Button
    private lateinit var usuarioSeleccionado: User

    private lateinit var viewModel: RegisterViewModel

    private lateinit var binding: ActivityRecicleRecuperarContraseniaBinding

    private lateinit var passwordCriteriaPopup: PasswordCriteriaPopup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = ActivityRecicleRecuperarContraseniaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicialización de los elementos de la UI
        inicializarElementosUI()

        // Configuración del ViewModel
        configurarViewModel()

        // Observar cambios en el estado de actualización
        observarEstadoActualizacion()

        // Inicializa el popup de criterios de contraseña
        passwordCriteriaPopup = PasswordCriteriaPopup(this)


        setupTextWatchers()

        // Manejador del evento clic en el botón guardar
        botonGuardar.setOnClickListener {
            procesarCambioContrasena()
        }
    }

    private fun inicializarElementosUI() {
        campoNuevaContrasena = binding.passwordRecuperacion
        campoConfirmarContrasena = binding.confirmPasswordConfirmacion
        botonGuardar = binding.savePasswordButton

        // Comprobar si el usuario seleccionado es nulo
        usuarioSeleccionado = (intent.getSerializableExtra("usuario_seleccionado") as? User) ?: return
    }


    private fun configurarViewModel() {
        val factory = UniversalViewModelFactory {
            RegisterViewModel(AppDatabase.getDatabase(this))
        }
        viewModel = ViewModelProvider(this, factory)[RegisterViewModel::class.java]
    }

    private fun observarEstadoActualizacion() {
        viewModel.estadoActualizacion.observe(this) { mensajeEstado ->
            Toast.makeText(this, mensajeEstado, Toast.LENGTH_SHORT).show()
            if (mensajeEstado == "Contraseña actualizada con éxito.") {
                finish() // Cierra la actividad si la contraseña se actualizó con éxito
            }
        }
    }

    private fun setupTextWatchers() {
        setupPasswordWatcher()
        setupRepeatPasswordWatcher()
    }

    private fun setupPasswordWatcher() {
        campoNuevaContrasena.addTextWatcher(onTextChangedAction = { s, _, _, _ ->
                passwordCriteriaPopup.mostrarPopup(campoNuevaContrasena)
                passwordCriteriaPopup.actualizarVistasCriterios(s.toString())

        })
    }


    private fun setupRepeatPasswordWatcher() {
        campoConfirmarContrasena.addTextWatcher(afterTextChangedAction = { s ->
             // Verificar si las contraseñas coinciden
            val coincide = s.toString() == campoNuevaContrasena.text.toString()
            campoConfirmarContrasena.error = if (!coincide) "Las contraseñas no coinciden." else null
        })
    }

    private fun EditText.addTextWatcher(
        beforeTextChangedAction: ((s: CharSequence?, start: Int, count: Int, after: Int) -> Unit)? = null,
        onTextChangedAction: ((s: CharSequence?, start: Int, before: Int, count: Int) -> Unit)? = null,
        afterTextChangedAction: ((s: Editable?) -> Unit)? = null
    ) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                beforeTextChangedAction?.invoke(s, start, count, after)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onTextChangedAction?.invoke(s, start, before, count)
            }

            override fun afterTextChanged(s: Editable?) {
                afterTextChangedAction?.invoke(s)
            }
        })
    }



    private fun procesarCambioContrasena() {
        val nuevaContrasena = campoNuevaContrasena.text.toString().trim()
        val confirmarContrasena = campoConfirmarContrasena.text.toString().trim()

        // Validación de campos de contraseña
        if (nuevaContrasena.isEmpty() || confirmarContrasena.isEmpty()) {
            Toast.makeText(this, "Las contraseñas no pueden estar vacías.", Toast.LENGTH_SHORT).show()
            return
        }

        if (nuevaContrasena != confirmarContrasena) {
            Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
            return
        }

        // Encriptación de la nueva contraseña y actualización
        val contrasenaEncriptada = BCrypt.hashpw(nuevaContrasena, BCrypt.gensalt())
        actualizarContrasena(contrasenaEncriptada)
    }

    private fun actualizarContrasena(contrasenaEncriptada: String) {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.actualizarContrasena(usuarioSeleccionado.username, contrasenaEncriptada)
        }
    }
}

