@file:Suppress("DEPRECATION")

package com.razen.laborbuddy.login

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.razen.laborbuddy.R
import com.razen.laborbuddy.inicio.PantallaPrincipal
import org.json.JSONObject


class InicioSesion : AppCompatActivity() {

    private lateinit var tvNombre: TextView
    private lateinit var tvHola: TextView

    private lateinit var changeUserButton: Button

    //creacion del canal
    private val idCanal = "notificacion Alarma"
    private val nombreCanal = "Notificacion de pronta Jornada"

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    private lateinit var requestQueue: RequestQueue

    //private lateinit var notiButton: Button
    private val prefs = "MyPrefs"
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    @SuppressLint("MissingPermission", "MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_sesion)
        requestQueue = Volley.newRequestQueue(this)


        //notiButton = findViewById(R.id.notificacion)

        //construir canal
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importancia = NotificationManager.IMPORTANCE_HIGH
            val canal = NotificationChannel(idCanal, nombreCanal, importancia)

            //manager de notificaciones

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            manager.createNotificationChannel(canal)

            /* notiButton.setOnClickListener {
                 val notificacion = NotificationCompat.Builder(this, idCanal).also { noti ->
                     noti.setSmallIcon(R.drawable.baseline_notification_important_24) // Agrega el ícono pequeño
                     noti.setContentTitle("La Jornada laboral empieza en 5 min")
                     noti.setContentText("Recuerda marcar tu Inicio de Jornada")
                 }.build()
                 val notificacionManager = NotificationManagerCompat.from(applicationContext)
                 notificacionManager.notify(1, notificacion)
             }*/
        }

        usernameEditText = findViewById(R.id.textUsuario)
        passwordEditText = findViewById(R.id.textPassword)
        loginButton = findViewById(R.id.loginButton)
        changeUserButton = findViewById(R.id.cambiarUser)
        tvNombre = findViewById(R.id.textBienvenida)
        tvHola = findViewById(R.id.textBienvenida1)

        // Crea un InputFilter para permitir solo letras y números en el campo de usuario
        val usernameInputFilter = InputFilter { source, start, end, dest, dstart, dend ->
            val regex = "^[a-zA-Z0-9]*$".toRegex()
            if (source.toString().matches(regex)) {
                null // Aceptar el input
            } else {
                "" // Rechazar el input
            }
        }

        // Aplica el InputFilter al EditText del campo de usuario
        usernameEditText.filters = arrayOf(usernameInputFilter)

        // Campo de contraseña sin restricciones
        // No es necesario aplicar ningún InputFilter al campo de contraseña, ya que se permite cualquier entrada.

        // Crear un InputFilter para limitar la longitud a 8 caracteres
        val maxLength = 8
        val inputFilter = InputFilter.LengthFilter(maxLength)

        // Aplicar el InputFilter de longitud al campo de usuario y al campo de contraseña si es necesario
        usernameEditText.filters = arrayOf(usernameInputFilter, inputFilter)
        passwordEditText.filters = arrayOf(inputFilter)

        // Configurar autenticación biométrica
        val executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor, authenticationCallback)
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación Biométrica")
            .setDescription("Escanea tu huella digital para autenticarte")
            .setNegativeButtonText("Cancelar")
            .build()


        val sharedPreferences = getSharedPreferences(prefs, Context.MODE_PRIVATE)
        val idUsuario = sharedPreferences.getString("idUsuario", "")
        val nombre = sharedPreferences.getString("nombre","")
        val apellidoP = sharedPreferences.getString("apellidoP","")
        val apellidoM = sharedPreferences.getString("apellidoM", "")

        if (idUsuario != null && idUsuario.isNotEmpty()){

            tvNombre.visibility = View.VISIBLE
            tvNombre.text = "$nombre\n$apellidoP $apellidoM"
            tvHola.visibility = View.VISIBLE

            //usernameEditText.setText(idUsuario).toString()
            passwordEditText.visibility = View.GONE // Ocultar campo de contraseña
            usernameEditText.visibility = View.GONE // Ocultar campo de usuario
            //loginButton.text = "Autenticar con Huella"
            loginButton.setOnClickListener {
                if (idUsuario.isNotEmpty()) {
                    // Mostrar diálogo de autenticación biométrica
                    biometricPrompt.authenticate(promptInfo)
                }
            }
            // Configura el botón para cambiar de usuario
            changeUserButton.setOnClickListener {

                // Obtén un editor de SharedPreferences para realizar cambios
                val editor = sharedPreferences.edit()
                // Limpia todos los valores en SharedPreferences
                editor.clear()
                // Aplica los cambios
                editor.apply()

                tvNombre.visibility = View.GONE
                tvHola.visibility = View.GONE
                changeUserButton.visibility = View.GONE
                loginButton.visibility = View.VISIBLE
                usernameEditText.visibility = View.VISIBLE
                passwordEditText.visibility = View.VISIBLE
                usernameEditText.text.clear()
                passwordEditText.text.clear()

                loginButton.setOnClickListener {
                    val username = usernameEditText.text.toString()
                    val password = passwordEditText.text.toString()

                    val usuario = username

                    if (siHayInternet()) {
                        if (username.isNotEmpty() && password.isNotEmpty()) {
                                validarUsuario(
                                    "https://benedictive-writing.000webhostapp.com/appAPI/validacion.php",
                                    usuario
                                )
                        } else {
                            Toast.makeText(
                                this@InicioSesion,
                                "Por favor, completa todos los campos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@InicioSesion,
                            "No tienes conexión a Internet. Verifica tu conexión e inténtalo de nuevo.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

            }
        }else{

            tvNombre.visibility = View.GONE
            tvHola.visibility = View.GONE
            changeUserButton.visibility = View.GONE
            loginButton.visibility = View.VISIBLE
            passwordEditText.visibility = View.VISIBLE
            usernameEditText.visibility = View.VISIBLE
            passwordEditText.visibility = View.VISIBLE

            loginButton.setOnClickListener {
                val username = usernameEditText.text.toString()
                val password = passwordEditText.text.toString()

                val usuario = username

                if (siHayInternet()) {
                    if (username.isNotEmpty() && password.isNotEmpty()) {
                       validarUsuario(
                                "https://benedictive-writing.000webhostapp.com/appAPI/validacion.php",
                                usuario
                            )
                    } else {
                        Toast.makeText(
                            this@InicioSesion,
                            "Por favor, completa todos los campos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@InicioSesion,
                        "No tienes conexión a Internet. Verifica tu conexión e inténtalo de nuevo.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }


        }

    }


    private val authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            val sharedPreferences = getSharedPreferences(prefs, Context.MODE_PRIVATE)
            val idUsuario = sharedPreferences.getString("idUsuario", "")
            //val password = passwordEditText.text.toString()
            if (idUsuario != null && idUsuario.isNotEmpty()){
                // Autenticación biométrica exitosa
                validarEstado(idUsuario)
            }
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            // Error en la autenticación biométrica
            Toast.makeText(this@InicioSesion, "Error en la autenticación biométrica: $errString", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validarEstado(idUsuario: String) {
        val apiUrl = "https://benedictive-writing.000webhostapp.com/appAPI/validarEstadoUsuario.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, apiUrl,
            Response.Listener { response ->
                val isUserEnabled = response == "1"

                if (isUserEnabled) {
                    // El usuario está habilitado
                    // El usuario seleccionó guardar la cuenta
                    val sharedPreferences = getSharedPreferences(prefs, Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("guardarCuenta", "true").apply()
                    guardarIdUsuario(idUsuario)
                    //obtenerDatosUsuario("https://aspcontrol1.000webhostapp.com/db_asp/mostrarDatos.php?id_usuario=$idUsuario")
                    obtenerDatosUsuario("https://benedictive-writing.000webhostapp.com/appAPI/mostrarDatos.php?id_usuario=$idUsuario")
                    // Llama a la función de inicio de sesión
                    ingresarApp()
                    // Puedes realizar otras acciones aquí si es necesario
                } else {
                    // El usuario está inhabilitado
                    Toast.makeText(this@InicioSesion, "Usuario Inhabilitado", Toast.LENGTH_SHORT).show()
                    // Puedes manejar lo que deseas hacer en caso de usuario inhabilitado
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this@InicioSesion, error.toString(), Toast.LENGTH_SHORT).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): MutableMap<String, String> {
                val parametros: MutableMap<String, String> = HashMap()
                parametros["idUsuario"] = idUsuario
                return parametros
            }
        }

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }


    private fun validarUsuario(url: String, usuario: String) {
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                when (response) {
                    "Acceso permitido" -> {
                        val idUsuario = usuario
                        // Obtiene una instancia de SharedPreferences
                        val sharedPreferences = getSharedPreferences(prefs, Context.MODE_PRIVATE)

                        // Obtiene un editor de SharedPreferences para realizar cambios
                        val editor = sharedPreferences.edit()
                        //editor.remove("idUsuario")
                        // Almacena la ID del usuario en SharedPreferences
                        editor.putString("idUsuario", idUsuario).apply()

                        if (deviceSupportsBiometrics()) {
                            // Mostrar diálogo de autenticación biométrica
                            mostrarDialogoGuardarCuenta(idUsuario)
                        } else {
                            ingresarApp()
                        }
                    }
                    "Contraseña incorrecta" -> {
                        Toast.makeText(this@InicioSesion, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                    }
                    "Usuario no encontrado o inhabilitado" -> {
                        Toast.makeText(this@InicioSesion, "Usuario no encontrado o inhabilitado", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this@InicioSesion, error.toString(), Toast.LENGTH_SHORT).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): MutableMap<String, String> {
                val parametros: MutableMap<String, String> = HashMap()
                parametros["usuario"] = usernameEditText.text.toString()
                parametros["password"] = passwordEditText.text.toString()
               return parametros
            }
        }

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    private fun ingresarApp(){
        val intent = Intent(applicationContext, PantallaPrincipal::class.java)
        startActivity(intent)

        Toast.makeText(this@InicioSesion, "Inicio de Sesion Exitoso", Toast.LENGTH_SHORT).show()
        // Cerrar la actividad de inicio de sesión
        finish()
    }

    private fun guardarIdUsuario(idUsuario: String) {
        // Obtiene una instancia de SharedPreferences
        val sharedPreferences = getSharedPreferences(prefs, Context.MODE_PRIVATE)

        // Obtiene un editor de SharedPreferences para realizar cambios
        val editor = sharedPreferences.edit()
        //editor.remove("idUsuario")
        // Almacena la ID del usuario en SharedPreferences
        editor.putString("idUsuario", idUsuario)

        // Aplica los cambios
        editor.apply()
    }

    @SuppressLint("SetTextI18n")
    private fun obtenerDatosUsuario(url: String) {
        val sharedPreferences = getSharedPreferences(prefs, Context.MODE_PRIVATE)

        val request = JsonObjectRequest(
            Request.Method.POST, url, null,
            { response ->
                Log.d("Response Inicio", response.toString())

                // Asegúrate de que la respuesta JSON se procese con la codificación UTF-8
                val jsonString = response.toString()
                val utf8String = String(jsonString.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)

                val jsonObject = JSONObject(utf8String)


                // Luego, obtén los valores de la respuesta
                val nombre = jsonObject.optString("nombres", "")
                val apellidoP = jsonObject.optString("apellido_P", "")
                val apellidoM = jsonObject.optString("apellido_M", "")
                val sexo = jsonObject.optString("sexo", "")
                val fechaNacimiento = jsonObject.optString("fecha_Nacimiento", "")
                val especialidad = jsonObject.optString("especialidad", "")
                val area = jsonObject.optString("cargo", "")
                val rol = jsonObject.optString("rol", "")


                // Obtiene un editor de SharedPreferences para realizar cambios
                val editor = sharedPreferences.edit()
                //limpia el almecenamiento
                //editor.clear().apply()
                // Almacena los datos del usuario en SharedPreferences
                editor.putString("nombre", nombre).apply()
                editor.putString("apellidoP", apellidoP).apply()
                editor.putString("apellidoM", apellidoM).apply()
                editor.putString("sexo", sexo).apply()
                editor.putString("fechaNacimiento", fechaNacimiento).apply()
                editor.putString("especialidad",especialidad).apply()
                editor.putString("area",area).apply()
                editor.putString("rol",rol).apply()
                Log.d("EDITOR", editor.toString())
                // Aplica los cambios

            },
            {
                // Mostrar mensaje de error
                /*Log.e("Error", error.toString()) // Agrega esta línea para imprimir el error en el log
                 tvNombre.text = "Error al obtener los datos: ${error.message}"
                 tvArea.text = "Error al obtener los datos: ${error.message}"*/
            }
        )

        // Agregar la solicitud a la cola de solicitudes
        Volley.newRequestQueue(this).add(request)
    }

    private fun mostrarDialogoGuardarCuenta(idUsuario:String) {
        val builder = AlertDialog.Builder(this)
        val sharedPreferences = getSharedPreferences(prefs, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        builder.setTitle("Guardar cuenta")
            .setMessage("¿Deseas guardar tu cuenta para un inicio de sesión más rápido la próxima vez?")
            .setPositiveButton("Guardar") { _, _ ->
                biometricPrompt.authenticate(promptInfo)

            }
            .setNegativeButton("No guardar") { _, _ ->
                // El usuario seleccionó no guardar la cuenta

                guardarIdUsuario(idUsuario)
                obtenerDatosUsuario("https://benedictive-writing.000webhostapp.com/appAPI/mostrarDatos.php?id_usuario=$idUsuario")

                ingresarApp()
                // Llama a la función de inicio de sesión
            }
            .setCancelable(false)
            .show()
    }

    private fun siHayInternet(): Boolean {
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun deviceSupportsBiometrics(): Boolean {
        val biometricManager = BiometricManager.from(this)
        val canAuthenticate = biometricManager.canAuthenticate()
        return canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS
    }
}
