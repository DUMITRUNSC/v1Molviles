package com.example.dumitrunascoproyecto.paginaInicio

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dumitrunascoproyecto.databinding.ActivityVistaUsuarioBinding
import com.example.dumitrunascoproyecto.principal.InicioSesionActivity

class PaginaInicio : AppCompatActivity() {

    private lateinit var binding: ActivityVistaUsuarioBinding

    private lateinit var fotoUsuario: ImageView
    private lateinit var usernameUsuario: TextView
    private lateinit var botonCerrarSesion: ImageButton


    // Dentro de la clase PaginaInicio

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVistaUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fotoUsuario = binding.fotoVistaUsuario
        usernameUsuario = binding.usernameVistaUsuario
        botonCerrarSesion = binding.btnCerrarSesion

        // Recuperar datos del Intent
        val username = intent.getStringExtra("username")
        val photoByteArray = intent.getByteArrayExtra("photo")

        // Establecer el nombre de usuario en TextView
        usernameUsuario.text = username

        // Convertir el ByteArray a Bitmap y establecerlo en ImageView
        if (photoByteArray != null) {
            val bitmap = BitmapFactory.decodeByteArray(photoByteArray, 0, photoByteArray.size)
            fotoUsuario.setImageBitmap(bitmap)
        }

         setuoListerins()

    }


    private fun bottonCeraraSesion() {
        botonCerrarSesion.setOnClickListener { cerrarSesion() }
    }

    private fun  cerrarSesion() {
        val intent = Intent(this, InicioSesionActivity::class.java)
        startActivity(intent)
        finish() // Finalizar esta actividad para evitar regresar a ella con el botón Atrás
    }

    private fun setuoListerins(){
        bottonCeraraSesion()
    }
}

