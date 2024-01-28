package com.example.dumitrunascoproyecto.admin

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dumitrunascoproyecto.database.AppDatabase
import com.example.dumitrunascoproyecto.database.RegisterViewModel
import com.example.dumitrunascoproyecto.database.UniversalViewModelFactory
import com.example.dumitrunascoproyecto.database.User
import com.example.dumitrunascoproyecto.databinding.ActivityAdminBinding
import com.example.dumitrunascoproyecto.principal.InicioSesionActivity

class AdminActivity : AppCompatActivity() {

    private lateinit var vistaBinding: ActivityAdminBinding
    private lateinit var viewModel: RegisterViewModel

    private lateinit var listaRecyclerView: RecyclerView
    private lateinit var adaptadorUsuario: UserAdapter
    private lateinit var botonEliminarTodos: Button
    private lateinit var botonCerraAdmin : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vistaBinding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(vistaBinding.root)

        inicializarUI()
        inicializarViewModel()
        configurarManejadorDeslizamiento()
        setupListerins()
    }

    private fun inicializarUI() {
        listaRecyclerView = vistaBinding.rvUsersList
        botonEliminarTodos = vistaBinding.btnDeleteAllUsers
        botonCerraAdmin = vistaBinding.btnExitAdmin
        listaRecyclerView.layoutManager = LinearLayoutManager(this)
        botonEliminarTodos.setOnClickListener {
            confirmarEliminarTodosLosUsuarios()
        }
    }

    private fun inicializarViewModel() {
        val factory = UniversalViewModelFactory {
            RegisterViewModel(AppDatabase.getDatabase(this))
        }
        viewModel = ViewModelProvider(this, factory)[RegisterViewModel::class.java]
        cargarUsuarios()
    }

    private fun cargarUsuarios() {
        viewModel.usuarios.observe(this) { usuarios ->
            adaptadorUsuario = UserAdapter(usuarios.toMutableList())
            listaRecyclerView.adapter = adaptadorUsuario
        }
        viewModel.obtenerTodosLosUsuarios()
    }

    private fun configurarManejadorDeslizamiento() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val user = adaptadorUsuario.obtenerUsuarioEnPosicion(position)
            if (direction == ItemTouchHelper.LEFT) {
                confirmarEliminar(user, position)
            } else if (direction == ItemTouchHelper.RIGHT) {
                confirmarCambioContrasena(user, position)

            }
        }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                dibujarFondoDeslizamiento(c, viewHolder, dX)
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(listaRecyclerView)
    }

    private fun confirmarEliminarTodosLosUsuarios() {
        AlertDialog.Builder(this).apply {
            setTitle("Confirmación")
            setMessage("¿Estás seguro de que quieres eliminar a todos los usuarios?")
            setPositiveButton("Sí") { _, _ ->
                adaptadorUsuario.limpiarUsuarios()
                viewModel.eliminarTodosLosUsuarios()
            }
            setNegativeButton("No", null)
            show()
        }
    }
    // Función para confirmar la eliminación de un usuario específico
    private fun confirmarEliminar(usuario: User, posicion: Int) {
        AlertDialog.Builder(this).apply {
            setTitle("Eliminar Usuario")
            setMessage("¿Quieres eliminar al usuario ${usuario.username}?")
            setPositiveButton("Sí") { _, _ ->
                adaptadorUsuario.removerUsuarioEnPosicion(posicion)
                viewModel.eliminarUsuario(usuario)
            }
            setNegativeButton("No") { _, _ ->
                adaptadorUsuario.notifyItemChanged(posicion) // Restaurar el elemento
            }
            show()
        }
    }

    // Función para confirmar el cambio de contraseña de un usuario
    private fun confirmarCambioContrasena(usuario: User, posicion: Int) {
        AlertDialog.Builder(this).apply {
            setTitle("Cambiar Contraseña")
            setMessage("¿Quieres cambiar la contraseña de ${usuario.username}?")
            setPositiveButton("Sí") { _, _ ->
                mostrarPantallaCambiarContrasena(usuario)
            }
            setNegativeButton("No") { _, _ ->
                adaptadorUsuario.notifyItemChanged(posicion) // Restaurar el elemento
            }
            show()
        }
    }

    // Función para mostrar la pantalla de cambio de contraseña
    private fun mostrarPantallaCambiarContrasena(usuario: User) {
        val intent = Intent(this, CambiarContrasenaActivity::class.java)
        intent.putExtra("usuario_seleccionado", usuario)
        startActivity(intent)
    }

    // Función para dibujar el fondo del deslizamiento en el RecyclerView
    private fun dibujarFondoDeslizamiento(canvas: Canvas, viewHolder: RecyclerView.ViewHolder, dX: Float) {
        val paint = Paint()
        val itemView = viewHolder.itemView
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 40f // Ajusta este valor según sea necesario
            textAlign = Paint.Align.LEFT
        }

        if (dX > 0) {
            // Deslizamiento hacia la derecha
            paint.color = Color.GREEN
            val background = RectF(itemView.left.toFloat(), itemView.top.toFloat(), dX, itemView.bottom.toFloat())
            canvas.drawRect(background, paint)
            canvas.drawText("Modificar", itemView.left.toFloat() + 40f, itemView.top.toFloat() + itemView.height / 2 + 10, textPaint)
        } else if (dX < 0) {
            // Deslizamiento hacia la izquierda
            paint.color = Color.RED
            val background = RectF(itemView.right.toFloat() + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
            canvas.drawRect(background, paint)
            canvas.drawText("Eliminar", itemView.right.toFloat() - 220f, itemView.top.toFloat() + itemView.height / 2 + 10, textPaint)
        }
    }

    private fun bottonCeraraAdmin() {
        botonCerraAdmin.setOnClickListener {cerrarSesionAdmin()}
    }

    private fun  cerrarSesionAdmin() {
        val intent = Intent(this, InicioSesionActivity::class.java)
        startActivity(intent)
        finish() // Finalizar esta actividad para evitar regresar a ella con el botón Atrás
    }

    private fun setupListerins(){
        bottonCeraraAdmin()
    }
}






