package com.example.dumitrunascoproyecto.registrarse

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.dumitrunascoproyecto.database.AppDatabase
import com.example.dumitrunascoproyecto.database.RegisterViewModel
import com.example.dumitrunascoproyecto.database.UniversalViewModelFactory
import com.example.dumitrunascoproyecto.database.User
import com.example.dumitrunascoproyecto.databinding.ActivityRegistroBinding
import com.example.dumitrunascoproyecto.databinding.DialogTermsAndConditionsBinding
import com.example.dumitrunascoproyecto.inicioSesion.InicioSesion
import com.example.dumitrunascoproyecto.principal.InicioSesionActivity
import com.example.dumitrunascoproyecto.validaciones.Validator
import kotlinx.coroutines.*
import org.mindrot.jbcrypt.BCrypt
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class Registro : AppCompatActivity() {


    private lateinit var binding: ActivityRegistroBinding

    private lateinit var viewModel: RegisterViewModel

    private lateinit var editTextUsername: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextDate: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextRepeatPassword: EditText
    private lateinit var checkboxTerms: CheckBox
    private lateinit var buttonAvatar: Button
    private lateinit var buttonPhoto: Button
    private lateinit var buttonSave: Button

    private lateinit var passwordCriteriaPopup: PasswordCriteriaPopup

    private var photoByteArray: ByteArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicialización del ViewModel
        val factory = UniversalViewModelFactory {
            RegisterViewModel(AppDatabase.getDatabase(this))
        }
        viewModel = ViewModelProvider(this, factory)[RegisterViewModel::class.java]


        initializeViews()
        setupListeners()

        viewModel.estadoActualizacion.observe(this) { status ->
            Toast.makeText(this, status, Toast.LENGTH_SHORT).show()
            if (status == "Usuario guardado con éxito.") {
                val intent = Intent(this, InicioSesionActivity::class.java)
                startActivity(intent)
                finish() // Finalizar esta actividad para evitar regresar a ella con el botón Atrás
            }
            // Podrías manejar otros estados aquí si es necesario
        }
    }

    private fun initializeViews() {
        editTextUsername = binding.editTextUsername
        editTextEmail = binding.editTextEmail
        editTextDate = binding.editTextDate
        editTextPassword = binding.editTextPassword
        editTextRepeatPassword = binding.editTextRepeatPassword
        checkboxTerms = binding.checkboxTerms
        buttonAvatar = binding.buttonAvatar
        buttonPhoto = binding.buttonPhoto
        buttonSave = binding.buttonSave

        passwordCriteriaPopup = PasswordCriteriaPopup(this)

    }

    //#######################--- METODOS DE PULSACION ----###########################

    // --- Button de guardar usuarios
    private fun setupSaveButtonListener() {
        buttonSave.setOnClickListener { onSaveClicked() }
    }

    // --- Funcion para mostra calendario
    private fun setupDatePickerListener() {
        editTextDate.setOnClickListener { showDatePicker() }
    }

    // --- Button para llevara la clase Avatar para mostrar los avatars
    private fun setupAvatarButtonListener() {
        buttonAvatar.setOnClickListener { selectAvatar() }
    }

    // --- Button para abrir la camara
    private fun setupPhotoButtonListener() {
        buttonPhoto.setOnClickListener { requestCameraPermissions() }
    }

    // --- Checkbox para mostra terminos de condiciones
    private fun setupTermsCheckboxListener() {
        checkboxTerms.setOnClickListener {
            if (checkboxTerms.isChecked) {
                showTermsAndConditionsDialog()
            }
        }
    }

    // --- Metodo para llamar a los metodos anteriores
    private fun setupListeners() {
        setupSaveButtonListener()
        setupDatePickerListener()
        setupAvatarButtonListener()
        setupPhotoButtonListener()
        setupTermsCheckboxListener()
        setupTextWatchers()
    }

    //#######################--- HASH CONTRASENIA ----###########################

    private fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    //#######################--- BOTON DE GUARDAR REGISTRO ----###########################

    private fun onSaveClicked() {


        CoroutineScope(Dispatchers.IO).launch {

            if (validateFields()) {

                // Continuar con el proceso de registro si no hay coincidencias
                val hashedPassword = hashPassword(editTextPassword.text.toString())

                if (photoByteArray != null) {
                    saveUser(hashedPassword, photoByteArray)
                    Toast.makeText(this@Registro, "Guardado existosamente.", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Registro, "Por favor, selecciona un avatar o una foto o el usuario esta en uso.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    //#######################--- MOSTRAR CALENDARIO ----###########################

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, day ->
              handleDatePicked(year, month, day)
        }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]).show()

    }

    private fun handleDatePicked(year: Int, month: Int, day: Int) {
        val selectedDate = Calendar.getInstance().apply {
            set(year, month, day)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate.time)
        binding.editTextDate.setText(formattedDate)
    }


    //#######################--- AVATAR ----###########################

    private fun selectAvatar() {
        val intent = Intent(this, AvatarActivity::class.java)
        avatarLauncher.launch(intent)
    }


    private val avatarLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            photoByteArray = result.data?.getByteArrayExtra("avatar")
            // Aquí puedes actualizar una vista ImageView si quieres mostrar el avatar seleccionado
        }
    }

    //#######################--- CAMARA ----###########################

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            // Convertir el bitmap a ByteArray para almacenarlo en la base de datos
            photoByteArray = ImageUtils.bitmapToByteArray(bitmap, 90)
            // Aquí puedes actualizar una vista ImageView si quieres mostrar la foto
        }
    }

    private fun requestCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            cameraLauncher.launch(null)
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }

    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(this, "Se requiere permiso de cámara", Toast.LENGTH_LONG).show()
        }
    }

    //#######################--- VALIDACIONES ----###########################

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

    private fun setupTextWatchers() {
        setupUsernameWatcher()
        setupEmailWatcher()
        setupDateWatcher()
        setupPasswordWatcher()
        setupRepeatPasswordWatcher()
    }

    private fun setupUsernameWatcher() {
        editTextUsername.addTextWatcher(afterTextChangedAction = { s ->
            editTextUsername.error = if (!Validator.esNombreUsuarioValido(s.toString())) "El nombre de usuario debe tener entre 4 y 15 caracteres." else null
        })
    }


    private fun setupEmailWatcher() {
        editTextEmail.addTextWatcher(afterTextChangedAction = { s ->
            editTextEmail.error = if (!Validator.esCorreoValido(s.toString())) "Correo electrónico inválido." else null
        })
    }

    private fun setupDateWatcher() {
        editTextDate.addTextWatcher(afterTextChangedAction = { s ->
            editTextDate.error = if (!Validator.esFechaNacimientoValida(s.toString())) "La fecha de nacimiento no es válida o no eres mayor de 16 años." else null
        })
    }

    private fun setupPasswordWatcher() {
        editTextPassword.addTextWatcher(onTextChangedAction = { s, _, _, _ ->
            passwordCriteriaPopup.mostrarPopup(editTextPassword)
            passwordCriteriaPopup.actualizarVistasCriterios(s.toString())
        })
    }

    private fun setupRepeatPasswordWatcher() {
        editTextRepeatPassword.addTextWatcher(afterTextChangedAction = { s ->
            editTextRepeatPassword.error = if (s.toString() != editTextPassword.text.toString()) "Las contraseñas no coinciden." else null
        })
    }

    private fun validateFields(): Boolean {
        val username = editTextUsername.text.toString()
        val email = editTextEmail.text.toString()
        val fechaNacimiento = editTextDate.text.toString()
        val password = editTextPassword.text.toString()
        val repeatPassword = editTextRepeatPassword.text.toString()



        // Validar nombre de usuario
        if (!Validator.esNombreUsuarioValido(username)) {
            return false
        }

        // Validar correo electrónico
        if (!Validator.esCorreoValido(email)) {
            return false
        }

        // Validar contraseña
        if (!Validator.esContrasenaSegura(password)) {
            return false
        }

        // Comprobar si las contraseñas coinciden
        if (password != repeatPassword) {
            return false
        }

        // Validar fecha de nacimiento
        if (!Validator.esFechaNacimientoValida(fechaNacimiento)) {
            return false
        }


        return true
    }


        //#######################--- GUARDAR USUARIOS ----###########################

    private fun saveUser(hashedPassword: String, photoByteArray: ByteArray? = null) {
        val username = editTextUsername.text.toString()
        val isAdmin = username == "admin"

        // Crear un nuevo usuario con la foto seleccionada y el estado de administrador
        val newUser = User(
            username = username,
            email = editTextEmail.text.toString(),
            passwordHash = hashedPassword,
            fechaNacimiento = editTextDate.text.toString(),
            photo = photoByteArray,
            isAdmin = isAdmin // Asignar el estado de admin basado en el nombre de usuario
        )

        // Si el nombre de usuario es único, guardar el nuevo usuario en la base de datos y redirigir al usuario a la actividad PaginaInicio
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.guardarUsuario(newUser)
        }

    }



    //#######################--- CONDICIONES DE LA APP ----###########################

    private fun showTermsAndConditionsDialog() {
        // Inflar el layout del diálogo con View Binding
        val binding = DialogTermsAndConditionsBinding.inflate(layoutInflater)
        val dialogView = binding.root

        // Establecer el texto de los términos y condiciones
        binding.textViewTerms.text = readTermsAndConditions()

        // Crear y mostrar el diálogo
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // Manejar el evento del botón de aceptar
        binding.buttonAcceptTerms.setOnClickListener {
            this.binding.checkboxTerms.isChecked = true
            dialog.dismiss()
        }

        // Manejar el evento del botón de declinar
        binding.buttonDeclineTerms.setOnClickListener {
            this.binding.checkboxTerms.isChecked = false
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun readTermsAndConditions(): String {
        return try {
            assets.open("terms_and_conditions.txt").bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            e.printStackTrace()
            "Error al cargar los términos y condiciones."
        }
    }


}
