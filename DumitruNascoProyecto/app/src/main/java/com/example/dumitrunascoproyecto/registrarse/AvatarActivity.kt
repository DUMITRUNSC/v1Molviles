package com.example.dumitrunascoproyecto.registrarse

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.dumitrunascoproyecto.databinding.ActivityAvatarBinding
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream

class AvatarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAvatarBinding
    private lateinit var avatar1: ImageButton
    private lateinit var avatar2: ImageButton
    private lateinit var avatar3: ImageButton
    private lateinit var generar: Button
    private lateinit var byteArrayAvatar : ByteArray


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityAvatarBinding.inflate(layoutInflater)
            setContentView(binding.root)

            avatar1 = binding.imageButtonAvatar1
            avatar2 = binding.imageButtonAvatar2
            avatar3 = binding.imageButtonAvatar3
            generar = binding.buttonGenerarAvatar

            lifecycleScope.launch {
                cargarImagenes()
                configurarListenersAvatar()
                generar.setOnClickListener {
                    lifecycleScope.launch {
                        cargarImagenes()
                    }
                }
            }

        }

        private fun configurarListenersAvatar() {
            listOf(avatar1, avatar2, avatar3).forEach { imageView ->
                imageView.setOnClickListener { seleccionarAvatar(it as ImageView) }
            }
        }

        private fun seleccionarAvatar(imageView: ImageView) {
            imageView.drawable?.let {
                val bitmap = (it as BitmapDrawable).bitmap
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                byteArrayAvatar = stream.toByteArray()
                val returnIntent = Intent()
                returnIntent.putExtra("avatar", byteArrayAvatar)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
        }

    private suspend fun cargarImagenes() {
            try {
                val urlImage = obtenerUrlImagen()
                cargarImagenDesdeUrl(urlImage,  avatar1)
                val urlImage2 = obtenerUrlImagen()
                cargarImagenDesdeUrl(urlImage2, avatar2)
                val urlImage3 = obtenerUrlImagen()
                cargarImagenDesdeUrl(urlImage3, avatar3)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private suspend fun obtenerUrlImagen(): String {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://randomuser.me/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(RandomUserGeneratorApi::class.java)
            val respuesta= service.getRandomUser()
            return respuesta.results.first().picture.medium
        }

    private fun cargarImagenDesdeUrl(imageUrl: String?, imagenRandom: ImageButton) {
            Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .override(400, 400)
                .centerCrop()
                .into(imagenRandom)
        }
    }

