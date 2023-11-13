package com.razen.laborbuddy.secciones.cuenta

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.razen.laborbuddy.R
import com.razen.laborbuddy.login.InicioSesion

class CuentaFragment : Fragment(R.layout.fragment_cuenta) {

    private lateinit var sharedPreferences: SharedPreferences

    private val nombreLocal="nombre"
    private val apellidoPLocal="apellidoP"
    private val apellidoMLocal="apellidoM"
    private val areaLocal="area"
    private val especialidadLocal="especialidad"

    private lateinit var btnCerrarSesion: Button

    private lateinit var tvIdUsuario: TextView
    private lateinit var tvnombre: TextView
    private lateinit var tvapellidos: TextView
    private lateinit var tvespecialidad: TextView
    private lateinit var tvcargo: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cuenta, container, false)

        sharedPreferences = requireContext().getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)

        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion)

        tvIdUsuario = view.findViewById(R.id.idUsuario)
        tvnombre = view.findViewById(R.id.nombreUsuario)
        tvapellidos = view.findViewById(R.id.apellidoUsuario)
        tvespecialidad = view.findViewById(R.id.especialidadUsuario)
        tvcargo = view.findViewById(R.id.cargoUsuario)


        val nombreAlmc = sharedPreferences.getString(nombreLocal, "")
        val apellidoPAlmc = sharedPreferences.getString(apellidoPLocal, "")
        val apellidoMAlmc = sharedPreferences.getString(apellidoMLocal, "")
        val areaAlmc = sharedPreferences.getString(areaLocal, "")
        val especialidadAlmc = sharedPreferences.getString(especialidadLocal, "")
        val idUsuario = sharedPreferences.getString("idUsuario", "")


        if (idUsuario != "null") {
            tvIdUsuario.text = idUsuario
        }

        if (nombreAlmc != "null") {
            tvnombre.text = nombreAlmc
        }

        if (apellidoPAlmc != "null" && apellidoMAlmc != "null") {
            tvapellidos.text = "$apellidoPAlmc $apellidoMAlmc"
        }

        if (especialidadAlmc != "null") {
            tvespecialidad.text = especialidadAlmc
        }

        if (areaAlmc != "null") {
            tvcargo.text = areaAlmc
        }


        btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }


        return view
    }

    private fun cerrarSesion() {
        val guardarCuenta = sharedPreferences.getString("guardarCuenta", "")
        val editor = sharedPreferences.edit()

        // Aquí puedes realizar las acciones necesarias para cerrar la sesión
        if (guardarCuenta == "true"){
            // Luego, redirige al usuario a la pantalla de inicio de sesión
            val intent = Intent(requireContext(), InicioSesion::class.java)
            startActivity(intent)
            requireActivity().finish() // Opcional: finaliza la actividad actual para evitar que el usuario pueda volver atrás
        }else{
            //limpiar los datos de usuario
            editor.clear().apply()
            // Luego, redirige al usuario a la pantalla de inicio de sesión
            val intent = Intent(requireContext(), InicioSesion::class.java)
            startActivity(intent)
            requireActivity().finish() // Opcional: finaliza la actividad actual para evitar que el usuario pueda volver atrás
        }

    }
    companion object {
        private const val PREFS_FILE_NAME = "MyPrefs"
    }
}
