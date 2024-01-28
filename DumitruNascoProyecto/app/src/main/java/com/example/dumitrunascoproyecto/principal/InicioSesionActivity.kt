package com.example.dumitrunascoproyecto.principal

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dumitrunascoproyecto.databinding.ActivitySesionBinding
import com.example.dumitrunascoproyecto.inicioSesion.InicioSesion
import com.example.dumitrunascoproyecto.registrarse.Registro

class InicioSesionActivity : AppCompatActivity() {

    private lateinit var  binding :ActivitySesionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySesionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val botonInicioSesion = binding.inicio
        botonInicioSesion.setOnClickListener {
            val intent = Intent(this, InicioSesion::class.java)
            startActivity(intent)
        }

        val botonRegistrarse = binding.registrase
        botonRegistrarse.setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }
    }
}
