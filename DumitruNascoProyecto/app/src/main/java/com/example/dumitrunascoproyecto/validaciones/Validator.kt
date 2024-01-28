package com.example.dumitrunascoproyecto.validaciones

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Validator {


    companion object {


        fun esCorreoValido(email: String): Boolean {
            val patronCorreo = "^\\w+([-+.']\\w+)*@(gmail\\.com|hotmail\\.es)$"
            return email.matches(patronCorreo.toRegex())
        }

        fun esContrasenaSegura(password: String): Boolean {
            // Validación de longitud
            val criterioLongitud = password.length in 6..15

            // Validación de presencia de al menos una letra mayúscula
            val criterioMayuscula = password.any { it.isUpperCase() }

            // Validación de presencia de al menos una letra minúscula
            val criterioMinuscula = password.any { it.isLowerCase() }

            // Validación de presencia de al menos un número
            val criterioNumero = password.any { it.isDigit() }

            // Validación de presencia de al menos un carácter especial
            val criterioCaracterEspecial = password.any { it in "!@#$%^&*()-_=+[]{};:'\",<.>/?`~" }

            // Devuelve true si todos los criterios son verdaderos
            return criterioLongitud && criterioMayuscula && criterioMinuscula && criterioNumero && criterioCaracterEspecial
        }

        fun esNombreUsuarioValido(username: String): Boolean {
            return username.length in 4..15
        }

        private fun esFechaValida(fecha: Calendar): Boolean {
            val hoy = Calendar.getInstance()
            return !fecha.after(hoy)
        }

        private fun restriccionEdad(fecha: Calendar): Boolean {
            val restriccion = Calendar.getInstance().apply { add(Calendar.YEAR, -16)
            }
            return fecha.before(restriccion)
        }

        fun esFechaNacimientoValida(fechaNacimiento: String): Boolean {
            try {
                val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val fechaNac = formato.parse(fechaNacimiento) ?: return false

                val fechaNacCal = Calendar.getInstance().apply {
                    time = fechaNac
                }

                // Reutiliza las validaciones existentes
                return esFechaValida(fechaNacCal) && restriccionEdad(fechaNacCal)
            } catch (e: ParseException) {
                return false
            }
        }
    }
}
