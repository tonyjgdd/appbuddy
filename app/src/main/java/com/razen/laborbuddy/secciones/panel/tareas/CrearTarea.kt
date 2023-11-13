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
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.MultiAutoCompleteTextView
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CrearTarea : AppCompatActivity() {

    private lateinit var editTextNombre: EditText
    private lateinit var editTextDescripcion: EditText
    private lateinit var editTextFechaEntrega: EditText
    private lateinit var editTextHoraEntrega: EditText
    private lateinit var editTextEncargados: MultiAutoCompleteTextView

    private val cal = Calendar.getInstance()
    private val year = cal.get(Calendar.YEAR)
    private val month = cal.get(Calendar.MONTH)
    private val day = cal.get(Calendar.DAY_OF_MONTH)
    private val hour = cal.get(Calendar.HOUR_OF_DAY)
    private val minute = cal.get(Calendar.MINUTE)

    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private const val PREFS_FILE_NAME = "MyPrefs"
    }

    private lateinit var btnGuardarTarea: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_tarea)

        editTextNombre = findViewById(R.id.editTextTareaNombre)
        editTextDescripcion = findViewById(R.id.editTextDescripcion)
        editTextFechaEntrega = findViewById(R.id.datePickerEntrega)
        editTextHoraEntrega = findViewById(R.id.timePickerEntrega)
        editTextEncargados = findViewById(R.id.editTextEncargados)

        btnGuardarTarea = findViewById(R.id.buttonGuardarTarea)

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
        editTextEncargados.filters = arrayOf(inputFilter)

        // Configura el separador para múltiples destinatarios (en este caso, una coma)
        editTextEncargados.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())

        // Carga la lista de usuarios al MultiAutoCompleteTextView
        cargarListaUsuarios()

        sharedPreferences = this.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)

        val idCreador = sharedPreferences.getString("idUsuario", "")

        editTextFechaEntrega.setOnClickListener { showDatePickerDialog() }
        editTextHoraEntrega.setOnClickListener { showTimePickerDialog() }

        btnGuardarTarea.setOnClickListener {

            CrearTarea(idCreador.toString())


        }
    }

    @SuppressLint("NotConstructor")
    private fun CrearTarea(idCreador:String) {

        if (editTextNombre.text.isNullOrEmpty()) {
            mostrarMensaje("Nombre de la Tarea vacio")
            return
        }
        if (editTextEncargados.text.isNullOrEmpty()) {
            mostrarMensaje("Campo de encargado(s) vacio")
            return
        }

        val url = "https://benedictive-writing.000webhostapp.com/appAPI/crearTarea.php"
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)

        Log.d("URL de la solicitud", url)

        // Crear un HashMap para almacenar los datos de la tarea a enviar
        val params = HashMap<String, String>()
        params["idCreador"] = idCreador

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

        val fechaCreacion = getCurrentDate()
        params["fechaCreacion"] = fechaCreacion

        val horaCreacion = getCurrentTime()
        params["horaCreacion"] = horaCreacion

        // Si tienes la lista de usuarios asignados, puedes enviarla como JSON o de la manera que necesites en tu API.
        // Aquí se muestra cómo convertirla a una cadena JSON.
        if (!editTextEncargados.text.isNullOrEmpty()) {
            val usuariosAsignados = editTextEncargados.text.toString()
            params["usuariosAsignados"] = usuariosAsignados
        }


        Log.d("PARAMS: ", params.toString())

        // Realiza la solicitud solo si al menos un campo no es nulo ni vacío
        if (params.size > 1) {val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST,
            url,
            JSONObject(params as Map<*, *>?),
            { response ->
                if (response != null && response.has("status")) {
                    val status = response.getString("status")
                    val message = response.getString("message")
                    if (status == "success") {
                        // Tarea creada correctamente
                        mostrarMensaje(message)
                    } else {
                        // Manejo de error si la respuesta indica un error en el servidor
                        mostrarMensaje("Error: $message")
                    }
                } else {
                    mostrarMensaje("Respuesta inesperada del servidor")
                }
            },
            { error ->
                Log.d("SolicitudVolley", "Error en la solicitud: ${error.message}")
                mostrarMensaje("Error en la solicitud: ${error.message}")
            }
        )
            requestQueue.add(jsonObjectRequest)
        } else {
            // No hay campos para actualizar
            mostrarMensaje("No se creó la tarea, ya que todos son nulos o vacíos.")
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
                editTextEncargados.setAdapter(adapter)

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

    private fun getCurrentDate(): String {
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es"))
        return dateFormat.format(currentDate)
    }

    private fun getCurrentTime(): String {
        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return dateFormat.format(currentTime)
    }

}