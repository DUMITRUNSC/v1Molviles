package com.example.dumitrunascoproyecto.registrarse

import retrofit2.http.GET


interface RandomUserGeneratorApi {
    @GET("api/") // Asegúrate de que el endpoint sea correcto
    suspend fun getRandomUser(): RandomUserResponse
}

