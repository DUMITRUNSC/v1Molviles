package com.example.dumitrunascoproyecto.registrarse

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.example.dumitrunascoproyecto.databinding.ActivityPasswordCriteriaPopupBinding

class PasswordCriteriaPopup(private val context: Context) {

    private lateinit var binding: ActivityPasswordCriteriaPopupBinding
    private var ventanaPopup: PopupWindow? = null

    fun mostrarPopup(campo: EditText) {
        if (ventanaPopup == null) {
            // Infla usando View Binding
            binding = ActivityPasswordCriteriaPopupBinding.inflate(LayoutInflater.from(context))
            ventanaPopup = PopupWindow(binding.root, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        campo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Infla usando View Binding
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                actualizarVistasCriterios(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                // Infla usando View Binding
            }
        })

        ventanaPopup?.showAsDropDown(campo, 0, 10)
    }

    fun actualizarVistasCriterios(contrasenia: String) {
        val criterioLongitud = contrasenia.length in 6..15
        val criterioMayuscula = contrasenia.any { it.isUpperCase() }
        val criterioMinuscula = contrasenia.any { it.isLowerCase() }
        val criterioNumero = contrasenia.any { it.isDigit() }
        val criterioCaracterEspecial = contrasenia.any { it in "!@#$%^&*()-_=+[]{};:'\",<.>/?`~" }

        with(binding) {
            criteriaLength.setTextColor(if (criterioLongitud) Color.GREEN else Color.RED)
            criteriaUppercase.setTextColor(if (criterioMayuscula) Color.GREEN else Color.RED)
            criteriaLowercase.setTextColor(if (criterioMinuscula) Color.GREEN else Color.RED)
            criteriaNumber.setTextColor(if (criterioNumero) Color.GREEN else Color.RED)
            criteriaSpecialChar.setTextColor(if (criterioCaracterEspecial) Color.GREEN else Color.RED)

            if (criterioLongitud && criterioMayuscula && criterioMinuscula && criterioNumero && criterioCaracterEspecial) {
                ventanaPopup?.dismiss()
            }
        }
    }
}
