package com.razen.laborbuddy.secciones.panel.usuarios

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.razen.laborbuddy.R
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.HashMap
import java.util.Locale

class CrearUsuario : AppCompatActivity() {

    private lateinit var editTextNombre: EditText
    private lateinit var editTextApellidoPaterno: EditText
    private lateinit var editTextApellidoMaterno: EditText
    private lateinit var editTextIdUsuario: EditText
    private lateinit var editTextContrasena: EditText
    private lateinit var editTextEspecialidad: EditText
    private lateinit var editTextCargo: EditText
    private lateinit var editTextFechaNacimiento: EditText
    private lateinit var radioGroupSexo: RadioGroup
    private lateinit var radioButtonMasculino: RadioButton
    private lateinit var radioButtonFemenino:RadioButton
    private lateinit var buttonCrearUsuario: Button
    private lateinit var spinnerRol: Spinner // Agregamos el Spinner

    private var isDeleting = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_usuario)

        // Inicializa las vistas
        editTextNombre = findViewById(R.id.editTextNombre)
        editTextApellidoPaterno = findViewById(R.id.editTextApellidoPaterno)
        editTextApellidoMaterno = findViewById(R.id.editTextApellidoMaterno)
        editTextIdUsuario = findViewById(R.id.editTextIdUsuario)
        editTextContrasena = findViewById(R.id.editTextContrasena)
        editTextEspecialidad = findViewById(R.id.editTextEspecialidad)
        editTextCargo = findViewById(R.id.editTextCargo)
        editTextFechaNacimiento = findViewById(R.id.editTextFechaNacimiento)
        radioGroupSexo = findViewById(R.id.radioGroupSexo)
        radioButtonMasculino = findViewById(R.id.radioButtonMasculino)
        radioButtonFemenino = findViewById(R.id.radioButtonFemenino)
        buttonCrearUsuario = findViewById(R.id.buttonCrearUsuario)
        spinnerRol = findViewById(R.id.spinnerRol) // Inicializamos el Spinner

        // Crear un TextWatcher para el campo de fecha de nacimiento
// Crear un TextWatcher para el campo de fecha de nacimiento
        val dateOfBirthTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No es necesario implementar este método
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No es necesario implementar este método
            }

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()

                // Detectar si se está eliminando caracteres
                if (isDeleting) {
                    // Borrar todo el campo de fecha de nacimiento
                    editTextFechaNacimiento.text.clear()
                } else {
                    if (text.length == 2 || text.length == 5) {
                        // Agregar automáticamente "/" después de los dos primeros dígitos y después de los cinco dígitos
                        val formattedText = text + "/"
                        editTextFechaNacimiento.removeTextChangedListener(this)
                        editTextFechaNacimiento.setText(formattedText)
                        editTextFechaNacimiento.setSelection(formattedText.length)
                        editTextFechaNacimiento.addTextChangedListener(this)
                    }

                    if (text.length > 10) {
                        // Limitar la cantidad de caracteres a 10
                        val formattedText = text.substring(0, 10)
                        editTextFechaNacimiento.removeTextChangedListener(this)
                        editTextFechaNacimiento.setText(formattedText)
                        editTextFechaNacimiento.setSelection(formattedText.length)
                        editTextFechaNacimiento.addTextChangedListener(this)
                    }
                }

                // Restablecer el valor de "isDeleting" a falso
                isDeleting = false
            }
        }

        // Asociar el TextWatcher al campo de fecha de nacimiento
        editTextFechaNacimiento.addTextChangedListener(dateOfBirthTextWatcher)

        // Configura un listener para detectar el evento onDelete
        editTextFechaNacimiento.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                // El usuario intenta eliminar caracteres, establecer "isDeleting" a verdadero
                isDeleting = true
            }
            false
        })


        // Crear un InputFilter para permitir solo letras y espacios
        val lettersAndSpecialCharsFilter = InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if (!Character.isLetter(source[i]) &&
                    !Character.isWhitespace(source[i]) &&
                    source[i] != 'ñ' && source[i] != 'Ñ' && source[i] != 'á' && source[i] != 'é' &&
                    source[i] != 'í' && source[i] != 'ó' && source[i] != 'ú' && source[i] != 'Á' &&
                    source[i] != 'É' && source[i] != 'Í' && source[i] != 'Ó' && source[i] != 'Ú') {
                    return@InputFilter ""
                }
            }
            null // Aceptar el input
        }
        // Aplicar el InputFilter a los campos de nombre, apellido paterno, apellido materno, especialidad y cargo
        editTextNombre.filters = arrayOf(lettersAndSpecialCharsFilter)
        editTextApellidoPaterno.filters = arrayOf(lettersAndSpecialCharsFilter)
        editTextApellidoMaterno.filters = arrayOf(lettersAndSpecialCharsFilter)
        editTextEspecialidad.filters = arrayOf(lettersAndSpecialCharsFilter)
        editTextCargo.filters = arrayOf(lettersAndSpecialCharsFilter)

        // Crear un InputFilter para idUsuario (letras y números, máximo 6 caracteres)
        val idUsuarioFilter = InputFilter { source, start, end, dest, dstart, dend ->
            val input = source.subSequence(start, end).toString()
            if (input.matches("[a-zA-Z0-9ñÑáéíóúÁÉÍÓÚ]*".toRegex()) && dest.length + input.length - (dend - dstart) <= 6) {
                null // Aceptar el input
            } else {
                "" // Rechazar el input
            }
        }

// Aplicar el InputFilter al campo de idUsuario
        editTextIdUsuario.filters = arrayOf(idUsuarioFilter)

// Crear un InputFilter para la contraseña (máximo 8 caracteres)
        val passwordFilter = InputFilter.LengthFilter(8)

// Aplicar el InputFilter al campo de contraseña
        editTextContrasena.filters = arrayOf(passwordFilter)

        // Define las opciones para el Spinner (usuario y administrador)
        val opciones = arrayOf("Usuario", "Administrador")

        // Crea un ArrayAdapter para el Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)

        // Configura el estilo del dropdown del Spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Asigna el ArrayAdapter al Spinner
        spinnerRol.adapter = adapter

        // Configura el listener de clic para el botón "Crear Usuario"
        buttonCrearUsuario.setOnClickListener {
            crearUsuario()
        }
        // Configura un listener para el Spinner para determinar el valor seleccionado
        spinnerRol.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Obtiene el valor seleccionado
                val selectedItem = parent?.getItemAtPosition(position).toString()

                // Define una variable para el valor numérico
                val valorNumerico = if (selectedItem == "Usuario") 1 else 2

                // Puedes agregar código para hacer algo con el valor numerico si es necesario

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Manejar el caso en el que no se seleccione nada
            }
        }
    }

    private fun crearUsuario() {
        // Obtiene los datos del formulario
        val nombre = editTextNombre.text.toString().trim()
        val apellidoPaterno = editTextApellidoPaterno.text.toString().trim()
        val apellidoMaterno = editTextApellidoMaterno.text.toString().trim()
        val idUsuario = editTextIdUsuario.text.toString().trim()
        val contrasena = editTextContrasena.text.toString().trim()
        val especialidad = editTextEspecialidad.text.toString().trim()
        val cargo = editTextCargo.text.toString().trim()
        val fechaNacimiento = editTextFechaNacimiento.text.toString().trim()
        val sexo = if (radioButtonMasculino.isChecked) "Masculino" else "Femenino"
        val rol = if (spinnerRol.selectedItem.toString() == "Usuario") 1 else 2

        // Valida que los campos no estén vacíos
        if (nombre.isEmpty() || apellidoPaterno.isEmpty() || idUsuario.isEmpty() ||
            contrasena.isEmpty() || especialidad.isEmpty() || cargo.isEmpty() ||
            fechaNacimiento.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Logs para rastrear datos antes de la solicitud
        Log.d("CrearUsuario", "Datos antes de la solicitud:")
        Log.d("CrearUsuario", "Nombre: $nombre")
        Log.d("CrearUsuario", "Apellido Paterno: $apellidoPaterno")
        Log.d("CrearUsuario", "Apellido Materno: $apellidoMaterno")
        Log.d("CrearUsuario", "ID de Usuario: $idUsuario")
        Log.d("CrearUsuario", "Contraseña: $contrasena")
        Log.d("CrearUsuario", "Especialidad: $especialidad")
        Log.d("CrearUsuario", "Cargo: $cargo")
        Log.d("CrearUsuario", "Fecha de Nacimiento: $fechaNacimiento")
        Log.d("CrearUsuario", "Sexo: $sexo")

        // URL de tu API para crear usuario
        val url = "https://benedictive-writing.000webhostapp.com/appAPI/crearUsuario.php"

        // Parámetros a enviar en la solicitud POST
        val params = HashMap<String, String>()
        params["nombres"] = nombre
        params["apellido_paterno"] = apellidoPaterno
        params["apellido_materno"] = apellidoMaterno
        params["id_usuario"] = idUsuario
        params["contrasena"] = contrasena
        params["especialidad"] = especialidad
        params["cargo"] = cargo
        params["fecha_nacimiento"] = fechaNacimiento
        params["sexo"] = sexo
        params["rol"] = rol.toString() // Agrega el parámetro "rol"

        // Configura la solicitud POST
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(Request.Method.POST, url,
            Response.Listener<String> { response ->
                // Manejar la respuesta exitosa
                Log.d("CrearUsuario", "Respuesta exitosa: $response")
                Toast.makeText(this, "Usuario creado exitosamente", Toast.LENGTH_SHORT).show()
                // Puedes agregar código para hacer otra acción después de crear el usuario
                // Puedes agregar código para regresar a la lista de mensajes o hacer otra acción
                finish() // Cierra el Activity actual
            },
            Response.ErrorListener { error ->
                // Manejar errores de la solicitud
                Log.e("CrearUsuario", "Error en la solicitud: ${error.message}")
                Toast.makeText(this, "Error al crear el usuario", Toast.LENGTH_SHORT).show()
                error.printStackTrace()
            }) {
            override fun getParams(): Map<String, String> {
                return params
            }
        }

        // Agrega la solicitud a la cola
        requestQueue.add(stringRequest)
    }
}
