package com.razen.laborbuddy.secciones.panel.tareas

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.MultiAutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.razen.laborbuddy.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DetallesTarea : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private const val PREFS_FILE_NAME = "MyPrefs"
    }

    private val cal = Calendar.getInstance()
    private val year = cal.get(Calendar.YEAR)
    private val month = cal.get(Calendar.MONTH)
    private val day = cal.get(Calendar.DAY_OF_MONTH)
    private val hour = cal.get(Calendar.HOUR_OF_DAY)
    private val minute = cal.get(Calendar.MINUTE)

    private lateinit var editTextFechaEntrega: EditText
    private lateinit var editTextHoraEntrega: EditText
    private lateinit var textViewNombre: TextView
    private lateinit var textViewCreador: TextView
    private lateinit var textViewFechaCreacion: TextView
    private lateinit var textViewHoraCreacion: TextView
    private lateinit var textViewFechaEntrega: TextView
    private lateinit var textViewHoraEntrega: TextView
    private lateinit var textViewEncargados: TextView
    private lateinit var textViewDescripcion: TextView
    private lateinit var textViewEstado: TextView
    private lateinit var editTextNombre: TextView
    private lateinit var multiAutoCompleteEncargados: MultiAutoCompleteTextView
    private lateinit var editTextDescripcion: TextView

    private lateinit var buttonRealizado: Button
    private lateinit var buttonEditar: Button
    private lateinit var buttonGuardar: Button
    private lateinit var buttonCancelar: Button
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles_tarea)

        // Obtener los datos del mensaje de la intent
        val idTarea = intent.getStringExtra("id_Tarea")
        val nombre = intent.getStringExtra("nombre")
        val creador = intent.getStringExtra("id_Creador")
        val descripcion= intent.getStringExtra("descripcion")
        val fechaCreacion = intent.getStringExtra("fecha_Creacion")
        val horaCreacion = intent.getStringExtra("hora_Creacion")
        val fechaEntrega = intent.getStringExtra("fecha_Entrega")
        val horaEntrega = intent.getStringExtra("hora_Entrega")
        val encargados = intent.getStringExtra("encargados")
        val estado = intent.getStringExtra("estado")

        textViewNombre = findViewById(R.id.textViewNombreTarea)
        textViewCreador = findViewById(R.id.textViewCreador)
        textViewFechaCreacion = findViewById(R.id.textViewFechaCreacion)
        textViewHoraCreacion = findViewById(R.id.textViewHoraCreacion)
        textViewFechaEntrega = findViewById(R.id.textViewFechaEntrega)
        textViewHoraEntrega = findViewById(R.id.textViewHoraEntrega)
        textViewEncargados = findViewById(R.id.textViewEncargado)
        textViewDescripcion = findViewById(R.id.textViewDescipcion)
        textViewEstado = findViewById(R.id.textViewEstadoTarea)
        editTextNombre = findViewById(R.id.editTextNombreTarea)
        editTextFechaEntrega = findViewById(R.id.editTextFechaEntrega)
        editTextHoraEntrega = findViewById(R.id.editTextHoraEntrega)
        multiAutoCompleteEncargados = findViewById(R.id.multiCompleteEncargado)
        editTextDescripcion = findViewById(R.id.editTextDescripcion)

        textViewNombre.text = nombre
        editTextNombre.text = nombre
        textViewCreador.text = creador
        textViewFechaCreacion.text = fechaCreacion
        textViewHoraCreacion.text = horaCreacion
        textViewFechaEntrega.text = fechaEntrega
        editTextFechaEntrega.setText(fechaEntrega)
        textViewHoraEntrega.text = horaEntrega
        editTextHoraEntrega.setText(horaEntrega)
        textViewEncargados.text = encargados
        multiAutoCompleteEncargados.setText(encargados, TextView.BufferType.EDITABLE)
        textViewDescripcion.text = descripcion
        editTextDescripcion.text = descripcion
        textViewEstado.text = estado

        // Crear un InputFilter para permitir letras, números, espacios, comas, paréntesis, la "ñ" y las tildes
        val inputFilter = InputFilter { source, start, end, dest, dstart, dend ->
            val regex = "^[a-zA-Z0-9,() ñáéíóúÁÉÍÓÚ]*\$".toRegex()
            if (source.toString().matches(regex)) {
                null // Aceptar el input
            } else {
                "" // Rechazar el input
            }
        }
        // Aplicar el InputFilter al MultiAutoCompleteTextView
        multiAutoCompleteEncargados.filters = arrayOf(inputFilter)

        // Configura el separador para múltiples destinatarios (en este caso, una coma)
        multiAutoCompleteEncargados.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())

        // Carga la lista de usuarios al MultiAutoCompleteTextView
        cargarListaUsuarios()

        buttonEditar = findViewById(R.id.buttonEditarTarea)
        buttonCancelar = findViewById(R.id.buttonCancelarEditar)
        buttonRealizado = findViewById(R.id.buttonRealizado)
        buttonGuardar = findViewById(R.id.buttonGuardarTarea)

        sharedPreferences = this.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)

        val idUsuario = sharedPreferences.getString("idUsuario", "")

        if (idUsuario == creador) {
            buttonEditar.visibility = View.VISIBLE
            buttonCancelar.visibility = View.GONE
        }

        buttonEditar.setOnClickListener {
            textViewNombre.visibility = View.GONE
            editTextNombre.visibility = View.VISIBLE
            textViewEncargados.visibility = View.GONE
            multiAutoCompleteEncargados.visibility = View.VISIBLE
            textViewFechaEntrega.visibility = View.GONE
            editTextFechaEntrega.visibility = View.VISIBLE
            textViewHoraEntrega.visibility = View.GONE
            editTextHoraEntrega.visibility = View.VISIBLE
            textViewDescripcion.visibility = View.GONE
            editTextDescripcion.visibility = View.VISIBLE
            // Agregar clic listeners a los EditText de fecha y hora
            editTextFechaEntrega.setOnClickListener { showDatePickerDialog() }
            editTextHoraEntrega.setOnClickListener { showTimePickerDialog() }
            buttonEditar.visibility = View.GONE
            buttonGuardar.visibility = View.VISIBLE
            buttonRealizado.visibility = View.GONE
            buttonCancelar.visibility = View.VISIBLE
        }

        buttonCancelar.setOnClickListener {
            textViewNombre.visibility = View.VISIBLE
            editTextNombre.visibility = View.GONE
            textViewEncargados.visibility = View.VISIBLE
            multiAutoCompleteEncargados.visibility = View.GONE
            textViewFechaEntrega.visibility = View.VISIBLE
            editTextFechaEntrega.visibility = View.GONE
            textViewHoraEntrega.visibility = View.VISIBLE
            editTextHoraEntrega.visibility = View.GONE
            textViewDescripcion.visibility = View.VISIBLE
            editTextDescripcion.visibility = View.GONE
            buttonGuardar.visibility = View.GONE
            buttonEditar.visibility = View.VISIBLE
            buttonRealizado.visibility = View.VISIBLE
            buttonCancelar.visibility = View.GONE
        }

        buttonGuardar.setOnClickListener {
            textViewNombre.visibility = View.VISIBLE
            editTextNombre.visibility = View.GONE
            textViewEncargados.visibility = View.VISIBLE
            multiAutoCompleteEncargados.visibility = View.GONE
            textViewFechaEntrega.visibility = View.VISIBLE
            editTextFechaEntrega.visibility = View.GONE
            textViewHoraEntrega.visibility = View.VISIBLE
            editTextHoraEntrega.visibility = View.GONE
            textViewDescripcion.visibility = View.VISIBLE
            editTextDescripcion.visibility = View.GONE
            buttonGuardar.visibility = View.GONE
            buttonEditar.visibility = View.VISIBLE
            buttonRealizado.visibility = View.VISIBLE
            buttonCancelar.visibility = View.GONE
            ActualizarDatosTarea(idTarea)
            cargarListaUsuarios()
        }

    }

    private fun ActualizarDatosTarea(idTarea: String?) {

        if (editTextNombre.text.isNullOrEmpty()) {
            mostrarMensaje("Nombre de la Tarea vacio")
            return
        }
        if (multiAutoCompleteEncargados.text.isNullOrEmpty()) {
            mostrarMensaje("Campo de encargado(s) vacio")
            return
        }


        val url = "https://benedictive-writing.000webhostapp.com/appAPI/actualizarDatosTarea.php?idTarea=$idTarea"
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)

        Log.d("URL de la solicitud", url)

        // Crear un HashMap para almacenar los datos de la tarea a enviar
        val params = HashMap<String, String>()
        params["idTarea"] = idTarea.toString()

        if (!editTextNombre.text.isNullOrEmpty()) {
            params["nombreTarea"] = editTextNombre.text.toString()
        }

        if (!editTextDescripcion.text.isNullOrEmpty()) {
            params["descripcionTarea"] = editTextDescripcion.text.toString()
        }

        if (!editTextFechaEntrega.text.isNullOrEmpty()) {
            params["fechaEntrega"] = editTextFechaEntrega.text.toString()
        }

        if (!editTextHoraEntrega.text.isNullOrEmpty()) {
            params["horaEntrega"] = editTextHoraEntrega.text.toString()
        }

        // Si tienes la lista de usuarios asignados, puedes enviarla como JSON o de la manera que necesites en tu API.
        // Aquí se muestra cómo convertirla a una cadena JSON.
        if (!multiAutoCompleteEncargados.text.isNullOrEmpty()) {
            val usuariosAsignados = multiAutoCompleteEncargados.text.toString()
            params["usuariosAsignados"] = usuariosAsignados
        }

        Log.d("PARAMS: ", params.toString())

        // Realiza la solicitud solo si al menos un campo no es nulo ni vacío
        if (params.size > 1) {
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST,
                url,
                JSONObject(params as Map<*, *>?),
                { response ->
                    Log.d("SolicitudVolley", "Respuesta del servidor: $response")
                    Log.d("SolicitudVolley", "Respuesta del servidor: $response")
                    Log.d("SolicitudVolley", "Respuesta exitosa: $response")
                    mostrarMensaje("Datos de la tarea actualizados correctamente")
                    obtenerDatosTarea("https://benedictive-writing.000webhostapp.com/appAPI/obtenerDatosTarea.php?idTarea=$idTarea")
                },
                { error ->
                    Log.d("SolicitudVolley", "Error en la solicitud: ${error.message}")
                    mostrarMensaje("Error al actualizar los datos de la tarea: ${error.message}")
                }
            )

            requestQueue.add(jsonObjectRequest)
        } else {
            // No hay campos para actualizar
            mostrarMensaje("No se actualizaron datos de la tarea, ya que todos son nulos o vacíos.")
        }
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Formatea la fecha en el formato deseado
                val selectedDate = formatDate(selectedDay, selectedMonth, selectedYear)
                editTextFechaEntrega.setText(selectedDate)  // Usar setText
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                // Formatea la hora en el formato deseado
                val selectedTime = formatTime(selectedHour, selectedMinute)
                editTextHoraEntrega.setText(selectedTime)  // Usar setText
            },
            hour, minute, true
        )
        timePickerDialog.show()
    }

    private fun obtenerDatosTarea(url: String) {
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    // Procesa la respuesta asegurándote de que estás utilizando UTF-8
                    val jsonString = response.toString()
                    val jsonData = String(jsonString.toByteArray(Charset.forName("ISO-8859-1")), Charset.forName("UTF-8"))
                    val jsonObject = JSONObject(jsonData)

                    // Luego, extrae los datos del objeto JSON normalmente
                    val idTarea = if (jsonObject.has("id_Tarea")) jsonObject.getString("id_Tarea") else ""
                    val nombre = if (jsonObject.has("nombre")) jsonObject.getString("nombre") else ""
                    val creador = if (jsonObject.has("id_Creador")) jsonObject.getString("id_Creador") else ""
                    val fechaCreacion = if (jsonObject.has("fecha_Creacion")) jsonObject.getString("fecha_Creacion") else ""
                    val horaCreacion = if (jsonObject.has("hora_Creacion")) jsonObject.getString("hora_Creacion") else ""
                    val fechaEntrega = if (jsonObject.has("fecha_Entrega")) jsonObject.getString("fecha_Entrega") else ""
                    val horaEntrega = if (jsonObject.has("hora_Entrega")) jsonObject.getString("hora_Entrega") else ""
                    val descripcion = if (jsonObject.has("descripcion")) jsonObject.getString("descripcion") else ""
                    val estado = if (jsonObject.has("estado")) jsonObject.getString("estado") else ""

                    val encargadosArray = jsonObject.getJSONArray("usuarios_asignados")
                    val encargados = ArrayList<String>()

                    for (j in 0 until encargadosArray.length()) {
                        val idEncargado = encargadosArray.getString(j)
                        encargados.add(idEncargado)
                    }
                    // Crear un ArrayAdapter con la lista de encargados
                    val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, encargados)

                    // Asignar el adaptador al MultiAutoCompleteTextView
                    multiAutoCompleteEncargados.setAdapter(adapter)

                    // Configurar el separador para que sea una coma y un espacio
                    multiAutoCompleteEncargados.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())

                    // Configurar un escuchador para manejar la edición de la lista
                    multiAutoCompleteEncargados.setOnFocusChangeListener { _, hasFocus ->
                        if (!hasFocus) {
                            // El usuario ha terminado de editar la lista
                            val editedList = multiAutoCompleteEncargados.text.toString().split(", ").toMutableList()

                            // Actualiza la lista de encargados con los cambios realizados
                            encargados.clear()
                            encargados.addAll(editedList)

                            // Ahora encargados contiene la lista actualizada
                        }
                    }
                    // Crear un String a partir de la lista de encargados
                    val encargadosString = encargados.joinToString(", ")

                    // Mostrar la lista en el TextView
                    textViewEncargados.text = encargadosString

                    // Asignar datos a las vistas
                    textViewNombre.text = nombre
                    editTextNombre.text = nombre
                    textViewCreador.text = creador
                    textViewFechaCreacion.text = fechaCreacion
                    textViewHoraCreacion.text = horaCreacion
                    textViewFechaEntrega.text = fechaEntrega
                    editTextFechaEntrega.setText(fechaEntrega)
                    textViewHoraEntrega.text = horaEntrega
                    editTextHoraEntrega.setText(horaEntrega)
                    textViewDescripcion.text = descripcion
                    editTextDescripcion.text = descripcion
                    textViewEstado.text = estado

                    // Puedes realizar otras acciones o procesar datos adicionales si es necesario.

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            {
                // Maneja errores
                Log.e("Error", it.toString())
                mostrarMensaje("Error al obtener los datos de la tarea: ${it.message}")
            }
        )

        // Agregar la solicitud a la cola de solicitudes
        Volley.newRequestQueue(this).add(request)
    }

    private fun formatDate(day: Int, month: Int, year: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        val dateFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun formatTime(hour: Int, minute: Int): String {
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        return timeFormat.format(calendar.time)
    }
    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    private fun cargarListaUsuarios() {
        // URL de tu API para obtener la lista de usuarios
        val urlUsuarios = "https://benedictive-writing.000webhostapp.com/appAPI/obtenerUsuarios.php"

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, urlUsuarios, null,
            { response ->
                // Procesa la respuesta JSON y obtén la lista de usuarios
                val listaUsuarios = obtenerListaUsuarios(response)

                // Configura un adaptador para el AutoCompleteTextView
                val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listaUsuarios)

                // Asocia el adaptador al AutoCompleteTextView
                multiAutoCompleteEncargados.setAdapter(adapter)

                // Agrega un Log para verificar si se obtienen los usuarios correctamente
                Log.d("CargarUsuarios", "Usuarios obtenidos: $listaUsuarios")
            },
            { error ->
                // Manejar errores de la solicitud
                Toast.makeText(this, "Error al cargar la lista de usuarios", Toast.LENGTH_SHORT).show()
                error.printStackTrace()
            }
        )

        // Agrega la solicitud a la cola
        requestQueue.add(jsonArrayRequest)
    }

    private fun obtenerListaUsuarios(response: JSONArray): ArrayList<String> {
        val listaUsuarios = ArrayList<String>()
        for (i in 0 until response.length()) {
            try {
                val usuarioJSON = response.getJSONObject(i)
                val nombres = usuarioJSON.getString("nombres")
                val apellidoP = usuarioJSON.getString("apellido_P")
                val idUsuario = usuarioJSON.getString("id_Usuario")

                // Combina nombres y apellido_P en un solo nombre de usuario
                val nombreUsuario = idUsuario

                listaUsuarios.add(nombreUsuario)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return listaUsuarios
    }
}