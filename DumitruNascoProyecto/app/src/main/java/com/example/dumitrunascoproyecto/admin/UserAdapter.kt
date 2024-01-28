package com.example.dumitrunascoproyecto.admin

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dumitrunascoproyecto.database.User
import com.example.dumitrunascoproyecto.databinding.ActivityListasUsuariosAdminBinding

class UserAdapter(private val listaUsuarios: MutableList<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    // ViewHolder que contiene la lógica para vincular un objeto User a un elemento de lista
    class UserViewHolder(private val binding: ActivityListasUsuariosAdminBinding) : RecyclerView.ViewHolder(binding.root) {
        fun vincular(usuario: User) {
            // Aquí se asignan los datos del usuario a los elementos de la vista
            binding.tvUsername.text = usuario.username
            binding.tvBirthDate.text = usuario.fechaNacimiento
            binding.tvEmail.text = usuario.email
            usuario.photo?.let {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                binding.ivUserPhoto.setImageBitmap(bitmap)
            }
        }
    }

    // Crea nuevas vistas (invocado por el layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ActivityListasUsuariosAdminBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    // Reemplaza el contenido de una vista (invocado por el layout manager)
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val usuario = listaUsuarios[position]
        holder.vincular(usuario)
    }

    // Retorna el tamaño de tu dataset (invocado por el layout manager)
    override fun getItemCount(): Int = listaUsuarios.size

    fun limpiarUsuarios() {
        listaUsuarios.clear()
        notifyDataSetChanged() // Notifica que los datos han cambiado
    }

    // Remueve un usuario de una posición específica
    fun removerUsuarioEnPosicion(posicion: Int) {
        listaUsuarios.removeAt(posicion)
        notifyItemRemoved(posicion)
    }

    // Obtiene un usuario en una posición específica
    fun obtenerUsuarioEnPosicion(posicion: Int): User {
        return listaUsuarios[posicion]
    }
}
