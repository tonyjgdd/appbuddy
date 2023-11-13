package com.razen.laborbuddy.secciones.panel.usuarios

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.razen.laborbuddy.R
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset

class DatosUsuario : AppCompatActivity() {

    private lateinit var spinnerRol: Spinner
    private lateinit var spinnerSexo: Spinner
    private lateinit var textViewEstado: TextView
    private lateinit var textViewID: TextView
    private lateinit var textViewNombre: TextView
    private lateinit var textViewApellidoPaterno: TextView
    private lateinit var textViewApellidoMaterno: TextView
    private lateinit var textViewSexo: TextView
    private lateinit var textViewFechaNacimiento: TextView
    private lateinit var textViewProfesion: TextView
    private lateinit var textViewCargo: TextView
    private lateinit var textViewRol: TextView
    private lateinit var editTextNombre: TextView
    private lateinit var editTextApellidoPaterno: TextView
    private lateinit var editTextApellidoMaterno: TextView
    private lateinit var editTextFechaNacimiento: TextView
    private lateinit var editTextProfesion: TextView
    private lateinit var editTextCargo: TextView
    private lateinit var buttonEditar: Button
    private lateinit var buttonHabilitarInhabilitar: Button
    private lateinit var buttonCancelar: Button

    private var isEditing = false
    private var isUserEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datos_usuario)

        // Inicializar vistas
        textViewEstado = findViewById(R.id.textViewEstado)
        textViewID = findViewById(R.id.textViewID)
        textViewNombre = findViewById(R.id.textViewNombre)
        textViewApellidoPaterno = findViewById(R.id.textViewApellidoPaterno)
        textViewApellidoMaterno = findViewById(R.id.textViewApellidoMaterno)
        textViewSexo = findViewById(R.id.textViewSexo)
        textViewFechaNacimiento = findViewById(R.id.textViewFechaNacimiento)
        textViewProfesion = findViewById(R.id.textViewProfesion)
        textViewCargo = findViewById(R.id.textViewCargo)
        textViewRol = findViewById(R.id.textViewRol)
        editTextNombre = findViewById(R.id.editTextNombre)
        editTextApellidoPaterno = findViewById(R.id.editTextApellidoPaterno)
        editTextApellidoMaterno = findViewById(R.id.editTextApellidoMaterno)
        editTextFechaNacimiento = findViewById(R.id.editTextFechaNacimiento)
        editTextProfesion = findViewById(R.id.editTextProfesion)
        editTextCargo = findViewById(R.id.editTextCargo)
        buttonEditar = findViewById(R.id.buttonEditar)
        buttonHabilitarInhabilitar = findViewById(R.id.buttonHabilitarInhabilitar)
        buttonCancelar = findViewById(R.id.buttonCancelar)


        // Inicializar los Spinners
        spinnerRol = findViewById(R.id.spinnerRolUser)
        spinnerSexo = findViewById(R.id.spinnerSexo)

        // Configurar los adaptadores para los Spinners
        val rolesAdapter = ArrayAdapter.createFromResource(this, R.array.roles, android.R.layout.simple_spinner_item)
        rolesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRol.adapter = rolesAdapter

        val sexosAdapter = ArrayAdapter.createFromResource(this, R.array.sexos, android.R.layout.simple_spinner_item)
        sexosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSexo.adapter = sexosAdapter

        // Obtener el ID del usuario del Intent
        val idUsuario = intent.getStringExtra("idUsuario")
        textViewID.text = idUsuario

        // Cargar datos del usuario
        obtenerDatosUsuario("https://benedictive-writing.000webhostapp.com/appAPI/mostrarDatos.php?id_usuario=$idUsuario")

        // Configurar el botón de editar
        // Cambia el estado de edición al hacer clic en el botón "Editar"
        buttonEditar.setOnClickListener {
            isEditing = !isEditing // Cambia el estado de edición

            if (isEditing) {
                // Si está en modo edición, muestra los EditTexts y oculta los TextViews
                textViewNombre.visibility = View.GONE
                editTextNombre.visibility = View.VISIBLE
                textViewApellidoPaterno.visibility = View.GONE
                editTextApellidoPaterno.visibility = View.VISIBLE
                textViewApellidoMaterno.visibility = View.GONE
                editTextApellidoMaterno.visibility = View.VISIBLE
                textViewSexo.visibility = View.GONE
                spinnerSexo.visibility = View.VISIBLE
                textViewFechaNacimiento.visibility = View.GONE
                editTextFechaNacimiento.visibility = View.VISIBLE
                textViewProfesion.visibility = View.GONE
                editTextProfesion.visibility = View.VISIBLE
                textViewCargo.visibility = View.GONE
                editTextCargo.visibility = View.VISIBLE
                textViewRol.visibility = View.GONE
                spinnerRol.visibility = View.VISIBLE
                buttonCancelar.visibility = View.VISIBLE

                buttonCancelar.setOnClickListener {
                    isEditing = !isEditing // Cambia el estado de edición
                    textViewNombre.visibility = View.VISIBLE
                    editTextNombre.visibility = View.GONE
                    textViewApellidoPaterno.visibility = View.VISIBLE
                    editTextApellidoPaterno.visibility = View.GONE
                    textViewApellidoMaterno.visibility = View.VISIBLE
                    editTextApellidoMaterno.visibility = View.GONE
                    textViewSexo.visibility = View.VISIBLE
                    spinnerSexo.visibility = View.GONE
                    textViewFechaNacimiento.visibility = View.VISIBLE
                    editTextFechaNacimiento.visibility = View.GONE
                    textViewProfesion.visibility = View.VISIBLE
                    editTextProfesion.visibility = View.GONE
                    textViewCargo.visibility = View.VISIBLE
                    editTextCargo.visibility = View.GONE
                    textViewRol.visibility = View.VISIBLE
                    spinnerRol.visibility = View.GONE
                    buttonCancelar.visibility = View.GONE
                    buttonEditar.text = if (isEditing) "Guardar" else "Editar" // Cambia el texto del botón

                }


            } else {
                // Si no está en modo edición, muestra los TextViews y oculta los EditTexts
                textViewNombre.visibility = View.VISIBLE
                editTextNombre.visibility = View.GONE
                textViewApellidoPaterno.visibility = View.VISIBLE
                editTextApellidoPaterno.visibility = View.GONE
                textViewApellidoMaterno.visibility = View.VISIBLE
                editTextApellidoMaterno.visibility = View.GONE
                textViewSexo.visibility = View.VISIBLE
                spinnerSexo.visibility = View.GONE
                textViewFechaNacimiento.visibility = View.VISIBLE
                editTextFechaNacimiento.visibility = View.GONE
                textViewProfesion.visibility = View.VISIBLE
                editTextProfesion.visibility = View.GONE
                textViewCargo.visibility = View.VISIBLE
                editTextCargo.visibility = View.GONE
                textViewRol.visibility = View.VISIBLE
                spinnerRol.visibility = View.GONE
                buttonCancelar.visibility = View.GONE

                // Cuando se hace clic en "Guardar", envía los datos a la API
                val selectedRol = spinnerRol.selectedItem.toString()
                val selectedSexo = spinnerSexo.selectedItem.toString()

                // Map para convertir roles a valores 0 o 1
                val rolMap = mapOf("Administrador" to "1", "Usuario" to "0")
                // Map para convertir sexos a masculino o femenino
                val sexoMap = mapOf("Masculino" to "Masculino", "Femenino" to "Femenino")

                val rolParam = rolMap[selectedRol]
                val sexoParam = sexoMap[selectedSexo]

                if (idUsuario != null) {
                    enviarDatosALaAPI(idUsuario, rolParam, sexoParam)
                }
            }

            buttonEditar.text = if (isEditing) "Guardar" else "Editar" // Cambia el texto del botón
        }

        // Configurar el botón de habilitar/inhabilitar
// ...
        buttonHabilitarInhabilitar.setOnClickListener {
            val nuevoEstado = if (isUserEnabled) "0" else "1"
            val confirmMessage = if (isUserEnabled) getString(R.string.confirm_inhabilitar_usuario) else getString(R.string.confirm_habilitar_usuario)

            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Confirmación")
            dialog.setMessage(confirmMessage)
            dialog.setPositiveButton("Sí") { _, _ ->
                // El usuario confirmó la acción, puedes proceder con la habilitación o inhabilitación
                isUserEnabled = !isUserEnabled
                if (idUsuario != null) {
                    actualizarEstadoUsuario(nuevoEstado, idUsuario)
                }

                if (isUserEnabled) {
                    buttonHabilitarInhabilitar.text = "Inhabilitar"
                    textViewEstado.text = "Habilitado"
                } else {
                    buttonHabilitarInhabilitar.text = "Habilitar"
                    textViewEstado.text = "Inhabilitado"
                }
                mostrarMensaje("Estado del usuario actualizado.")
            }
            dialog.setNegativeButton("No") { _, _ ->
                // El usuario canceló la acción, no hagas nada
            }
            dialog.show()
        }

    }

    private fun actualizarEstadoUsuario(nuevoEstado: String, idUsuario: String) {
        // Aquí debes enviar una solicitud Volley para actualizar el estado del usuario en la API.
        // Reemplaza "URL_DE_TU_API" con la URL real de tu API.
        val url = "https://benedictive-writing.000webhostapp.com/appAPI/actualizarEstadoUsuario.php?idUsuario=$idUsuario"
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val parametros = HashMap<String, String>()
        parametros["estado"] = nuevoEstado
        Log.d("SolicitudVolley", "URL de la solicitud: $url")
        Log.d("SolicitudVolley", "Datos a enviar: $parametros")

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST,
            url,
            JSONObject((parametros as Map<*, *>?)!!),
            { response ->
                // Manejar la respuesta exitosa de la API (puedes mostrar un mensaje de éxito)
                Log.d("VolleyEstado", "Respuesta exitosa: $response")

                mostrarMensaje("Estado del usuario actualizado correctamente.")
            },
            { error ->
                // Manejar el error de la API (puedes mostrar un mensaje de error)
                Log.d("VolleyEstado", "Error en la solicitud: ${error.message}")

                mostrarMensaje("Error al actualizar el estado del usuario: ${error.message}")
            }
        )

        requestQueue.add(jsonObjectRequest)
    }

    private fun enviarDatosALaAPI(idUsuario: String, rolParam: String?, sexoParam: String?) {

        if (rolParam.isNullOrEmpty() && sexoParam.isNullOrEmpty()) {
            mostrarMensaje("rol o sexo invalido")
            return
        }

        if (idUsuario.isEmpty()) {
            mostrarMensaje("ID de usuario inválido")
            return
        }

        val url = "https://benedictive-writing.000webhostapp.com/appAPI/actualizarDatos.php?idUsuario=$idUsuario"
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)

        // Crear un HashMap para almacenar los datos a enviar
        val params = HashMap<String, String>()
        params["idUsuario"] = idUsuario


        if (!editTextNombre.text.isNullOrEmpty()) {
            params["nombres"] = editTextNombre.text.toString()
        }

        if (!editTextApellidoPaterno.text.isNullOrEmpty()) {
            params["apellidoPaterno"] = editTextApellidoPaterno.text.toString()
        }

        if (!editTextApellidoMaterno.text.isNullOrEmpty()) {
            params["apellidoMaterno"] = editTextApellidoMaterno.text.toString()
        }

        if (!sexoParam.isNullOrEmpty()) {
            params["sexo"] = sexoParam
        }

        if (!editTextFechaNacimiento.text.isNullOrEmpty()) {
            params["fechaNacimiento"] = editTextFechaNacimiento.text.toString()
        }

        if (!editTextProfesion.text.isNullOrEmpty()) {
            params["especialidad"] = editTextProfesion.text.toString()
        }

        if (!editTextCargo.text.isNullOrEmpty()) {
            params["area"] = editTextCargo.text.toString()
        }

        if (!rolParam.isNullOrEmpty()) {
            params["rol"] = rolParam
        }

        Log.d("PARAMS: ", params.toString())

        // Realiza la solicitud solo si al menos un campo no es nulo ni vacío
        if (params.size > 1) {
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST,
                url,
                (params as Map<*, *>?)?.let { JSONObject(it) },
                { response ->
                    try {
                        if (response.has("mensaje")) {
                            val mensaje = response.getString("mensaje")
                            Log.d("SolicitudVolley", "Respuesta exitosa: $mensaje")
                            mostrarMensaje(mensaje)
                            obtenerDatosUsuario("https://benedictive-writing.000webhostapp.com/appAPI/mostrarDatos.php?id_usuario=$idUsuario")
                        } else {
                            mostrarMensaje("Respuesta no válida de la API")
                        }
                    } catch (e: JSONException) {
                        mostrarMensaje("Error al analizar la respuesta JSON: ${e.message}")
                    }
                },
                { error ->
                    Log.d("SolicitudVolley", "Error en la solicitud: ${error.message}")
                    mostrarMensaje("Error al actualizar los datos: ${error.message}")
                }
            )

            requestQueue.add(jsonObjectRequest)
        } else {
            // No hay campos para actualizar
            mostrarMensaje("No se actualizaron datos, ya que todos son nulos o vacíos.")
        }
    }

    private fun obtenerDatosUsuario(url: String) {
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    // Procesa la respuesta asegurándote de que estás utilizando UTF-8
                    val jsonString = response.toString()
                    val jsonData = String(jsonString.toByteArray(Charset.forName("ISO-8859-1")), Charset.forName("UTF-8"))
                    val jsonObject = JSONObject(jsonData)

                    // Luego, extrae los datos del objeto JSON normalmente
                    val estado = if (jsonObject.has("estado")) jsonObject.getString("estado") else ""
                    val nombre = if (jsonObject.has("nombres")) jsonObject.getString("nombres") else ""
                    val apellidoP = if (jsonObject.has("apellido_P")) jsonObject.getString("apellido_P") else ""
                    val apellidoM = if (jsonObject.has("apellido_M")) jsonObject.getString("apellido_M") else ""
                    val sexo = if (jsonObject.has("sexo")) jsonObject.getString("sexo") else ""
                    val fechaNacimiento = if (jsonObject.has("fecha_Nacimiento")) jsonObject.getString("fecha_Nacimiento") else ""
                    val especialidad = if (jsonObject.has("especialidad")) jsonObject.getString("especialidad") else ""
                    val area = if (jsonObject.has("cargo")) jsonObject.getString("cargo") else ""
                    val rol = if (jsonObject.has("rol")) jsonObject.getString("rol") else ""


                    // Asignar datos a las vistas
                    textViewNombre.text = nombre
                    editTextNombre.text = nombre
                    textViewApellidoPaterno.text = apellidoP
                    editTextApellidoPaterno.text = apellidoP
                    textViewApellidoMaterno.text = apellidoM
                    editTextApellidoMaterno.text = apellidoM
                    textViewSexo.text = sexo
                    textViewFechaNacimiento.text = fechaNacimiento
                    editTextFechaNacimiento.text = fechaNacimiento
                    textViewProfesion.text = especialidad
                    editTextProfesion.text = especialidad
                    textViewCargo.text = area
                    editTextCargo.text = area

                    val estadoUser = if (estado == "1") "Habilitado" else "Inhabilitado"
                    textViewEstado.text = estadoUser

                    val sexosArray = resources.getStringArray(R.array.sexos)
                    val sexoIndex = if (sexo == "Masculino") sexosArray.indexOf("Masculino") else sexosArray.indexOf("Femenino")
                    spinnerSexo.setSelection(sexoIndex)

                    // Convertir el valor de 'rol' a 'Administrador' o 'Usuario'
                    val rolValor = if (rol == "1") "Administrador" else "Usuario"
                    textViewRol.text = rolValor

                    // Seleccionar el valor correcto en el Spinner de Rol
                    val rolesArray = resources.getStringArray(R.array.roles)
                    val rolIndex = if (rol == "1") rolesArray.indexOf("Administrador") else rolesArray.indexOf("Usuario")
                    spinnerRol.setSelection(rolIndex)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            {
                // Maneja errores
                Log.e("Error", it.toString())
                mostrarMensaje("Error al obtener los datos: ${it.message}")
            }
        )

        // Agregar la solicitud a la cola de solicitudes
        Volley.newRequestQueue(this).add(request)
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

}