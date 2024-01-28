package com.example.dumitrunascoproyecto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.dumitrunascoproyecto.databinding.ActivityLogoBinding
import com.example.dumitrunascoproyecto.principal.InicioSesionActivity

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityLogoBinding

    private val tiempo: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            // Inicia la actividad de inicio de sesión después de 3 segundos
            val intent = Intent(this, InicioSesionActivity::class.java)
            startActivity(intent)
            finish() // Finaliza esta actividad
        }, tiempo)
    }
}
