package com.razen.laborbuddy.secciones.panel.tareas

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import kotlin.collections.filter
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.razen.laborbuddy.R
import com.razen.laborbuddy.secciones.panel.mensajeria.MensajeEntrada
import com.razen.laborbuddy.secciones.panel.mensajeria.Mensajeria
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.DelayQueue

class Tareas : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var btnDesignar: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var tareasAdapter: TareasAdapter
    private val listaTareas = mutableListOf<Tarea>()
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    companion object {
        private const val PREFS_FILE_NAME = "MyPrefs"
        private val rol = "rol"
        private val id_Usuario = "idUsuario"
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tareas)


        sharedPreferences = this.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
        btnDesignar = findViewById(R.id.buttonDesignarTarea)
        recyclerView = findViewById(R.id.recyclerViewTareas)
        recyclerView.layoutManager = LinearLayoutManager(this)
        tareasAdapter = TareasAdapter(this, listaTareas)
        recyclerView.adapter = tareasAdapter

        val rolAlmc = sharedPreferences.getString(rol, "")
        val idUsuario = sharedPreferences.getString(id_Usuario, "")

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            // Lógica para recargar los datos
            if (idUsuario != null) {
                listaTareas.clear()  // Limpiar la lista existente
                obtenerTareasDesdeServidor(idUsuario)
            }
        }


        if (idUsuario != null) {
            obtenerTareasDesdeServidor(idUsuario)
        }

        if (rolAlmc == "1") {
            btnDesignar.visibility = View.VISIBLE
        } else {
            btnDesignar.visibility = View.GONE
        }

        btnDesignar.setOnClickListener {
            val intent = Intent(this, CrearTarea::class.java)
            startActivity(intent)
        }

        // Configurar el SearchView
        val searchView = findViewById<SearchView>(R.id.searchViewTareas)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                tareasAdapter.filter.filter(newText)
                return true
            }
        })



        tareasAdapter.setOnItemClickListener(object : TareasAdapter.OnItemClickListener{
            override fun onItemClick(tarea: Tarea) {
                val intent = Intent(this@Tareas, DetallesTarea::class.java)
                // Pasar los datos del mensaje al intent
                intent.putExtra("id_Tarea", tarea.id)
                intent.putExtra("nombre", tarea.nombre)
                intent.putExtra("id_Creador", tarea.creador)
                intent.putExtra("descipcion", tarea.descripcion)
                intent.putExtra("fecha_Creacion", tarea.fechaCreacion)
                intent.putExtra("hora_Creacion", tarea.horaCreacion)
                intent.putExtra("fecha_Entrega", tarea.fechaEntrega)
                intent.putExtra("hora_Entrega", tarea.horaEntrega)
                intent.putExtra("encargado", tarea.encargados)
                startActivity(intent)
            }
        })
    }

    private fun obtenerTareasDesdeServidor(idUsuario: String) {
        val url = "https://benedictive-writing.000webhostapp.com/appAPI/obtenerTareas.php?idUsuario=$idUsuario"

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response: JSONArray ->
                // Procesar la respuesta JSON para obtener datos de tareas
                procesarRespuestaJSON(response)
            },
            { error ->
                // Manejar errores de la solicitud Volley
                error.printStackTrace()
            }
        )

        requestQueue.add(jsonArrayRequest)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun procesarRespuestaJSON(response: JSONArray) {
        // Limpia la lista de tareas antes de agregar nuevos datos
        listaTareas.clear()

        for (i in 0 until response.length()) {
            val tareaJSON: JSONObject = response.getJSONObject(i)
            val id = tareaJSON.getString("id_Tarea")
            val nombre = tareaJSON.getString("nombre")
            val creador = tareaJSON.getString("id_Creador")
            val descripcion = tareaJSON.getString("descripcion")
            val fechaCreacion = tareaJSON.getString("fecha_Creacion")
            val horaCreacion = tareaJSON.getString("hora_Creacion")
            val fechaEntrega = tareaJSON.getString("fecha_Entrega")
            val horaEntrega = tareaJSON.getString("hora_Entrega")

            val encargadosArray = tareaJSON.getJSONArray("usuarios_asignados")
            val encargados = mutableListOf<String>()
            for (j in 0 until encargadosArray.length()) {
                val idEncargado = encargadosArray.getString(j)
                encargados.add(idEncargado)
            }

            // Crear un mensaje con los destinatarios en formato de lista separados por comas
            val encargadosComa = encargados.joinToString(", ")

            val estado = tareaJSON.getString("estado")
            val estadoTarea =if (estado == "0") "Pendiente" else "Realizado"

            val tarea = Tarea(id, nombre, creador, descripcion, fechaCreacion,horaCreacion,fechaEntrega,horaEntrega, encargadosComa, estadoTarea)
            listaTareas.add(tarea)
        }
        tareasAdapter.notifyDataSetChanged()
        tareasAdapter.filter.filter(null)
        // Detener la animación de actualización del SwipeRefreshLayout
        swipeRefreshLayout.isRefreshing = false
    }

    data class Tarea(
        val id: String,
        val nombre: String,
        val creador: String,
        val descripcion: String,
        val fechaCreacion: String,
        val horaCreacion: String,
        val fechaEntrega: String,
        val horaEntrega: String,
        val encargados: String,
        val estado: String
    )


    class TareasAdapter(
        private val context: Context,
        private val listaTareas: List<Tarea>
    ) : RecyclerView.Adapter<TareasAdapter.ViewHolder>(), Filterable {

        private val listaTareasOriginal: List<Tarea>
        private val listaTareasFiltrada = mutableListOf<Tarea>()

        init {
            listaTareasOriginal = listaTareas
            listaTareasFiltrada.addAll(listaTareasOriginal)
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val idTextView: TextView = itemView.findViewById(R.id.idTarea)
            val nombreTextView: TextView = itemView.findViewById(R.id.nombreTarea)
            val creadorTextView: TextView = itemView.findViewById(R.id.creadorTarea)

            init {
                // Agregar un OnClickListener para abrir DetallesTarea al hacer clic en un elemento
                itemView.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {


                        val tarea = listaTareasFiltrada[position]
                        val intent = Intent(context, DetallesTarea::class.java)
                        // Agregar registros (logs) para verificar los valores enviados
                        Log.d("TareasAdapter", "id_Tarea: ${tarea.id}")
                        Log.d("TareasAdapter", "nombre: ${tarea.nombre}")
                        Log.d("TareasAdapter", "id_Creador: ${tarea.creador}")
                        Log.d("TareasAdapter", "descripcion: ${tarea.descripcion}")
                        Log.d("TareasAdapter", "fecha_Creacion: ${tarea.fechaCreacion}")
                        Log.d("TareasAdapter", "hora_Creacion: ${tarea.horaCreacion}")
                        Log.d("TareasAdapter", "fecha_Entrega: ${tarea.fechaEntrega}")
                        Log.d("TareasAdapter", "hora_Entrega: ${tarea.horaEntrega}")
                        Log.d("TareasAdapter", "encargados: ${tarea.encargados}")
                        Log.d("TareasAdapter", "estado: ${tarea.estado}")
                        // Pasar los datos del mensaje al intent
                        intent.putExtra("id_Tarea", tarea.id)
                        intent.putExtra("nombre", tarea.nombre)
                        intent.putExtra("id_Creador", tarea.creador)
                        intent.putExtra("descripcion", tarea.descripcion)
                        intent.putExtra("fecha_Creacion", tarea.fechaCreacion)
                        intent.putExtra("hora_Creacion", tarea.horaCreacion)
                        intent.putExtra("fecha_Entrega", tarea.fechaEntrega)
                        intent.putExtra("hora_Entrega", tarea.horaEntrega)
                        intent.putExtra("encargados", tarea.encargados)
                        intent.putExtra("estado", tarea.estado)
                        // Agrega otros datos aquí si es necesario
                        context.startActivity(intent)
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.item_tarea, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val tarea = listaTareasFiltrada[position]
            holder.idTextView.text = tarea.id
            holder.nombreTextView.text = tarea.nombre
            holder.creadorTextView.text = tarea.creador
        }

        override fun getItemCount(): Int {
            return listaTareasFiltrada.size
        }

        private var onItemClickListener: OnItemClickListener? = null

        interface OnItemClickListener {
            fun onItemClick(tarea: Tarea)
        }

        fun setOnItemClickListener(listener: OnItemClickListener) {
            onItemClickListener = listener
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence?): FilterResults {
                    val query = constraint?.toString() ?: ""
                    val results = FilterResults()
                    val filteredList = mutableListOf<Tarea>()

                    if (query.isBlank()) {
                        filteredList.addAll(listaTareasOriginal)
                    } else {
                        for (tarea in listaTareasOriginal) {
                            if (tarea.nombre.contains(
                                    query,
                                    ignoreCase = true
                                ) || tarea.creador.contains(query, ignoreCase = true)
                            ) {
                                filteredList.add(tarea)
                            }
                        }
                    }

                    results.values = filteredList
                    results.count = filteredList.size
                    return results
                }

                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                    listaTareasFiltrada.clear()
                    listaTareasFiltrada.addAll(results?.values as List<Tarea>)
                    notifyDataSetChanged()
                }
            }
        }
    }
}