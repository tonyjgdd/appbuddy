package com.razen.laborbuddy.secciones.panel


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.razen.laborbuddy.secciones.panel.mensajeria.Mensajeria
import com.razen.laborbuddy.secciones.panel.tareas.Tareas
import com.razen.laborbuddy.secciones.panel.usuarios.Usuarios
import com.razen.laborbuddy.R


class PanelFragment : Fragment(R.layout.fragment_panel) {

    private lateinit var sharedPreferences: SharedPreferences

    private val nombreLocal="nombre"
    private val apellidoPLocal="apellidoP"
    private val apellidoMLocal="apellidoM"
    private val rol="rol"


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_panel, container, false)

        sharedPreferences = requireContext().getSharedPreferences(PanelFragment.PREFS_FILE_NAME, Context.MODE_PRIVATE)


        val tvNombre = view.findViewById<TextView>(R.id.nombre_textview)

        val cardMensajeria = view.findViewById<CardView>(R.id.cardMensajeria)
        val cardTareas = view.findViewById<CardView>(R.id.cardTareas)
        val cardUsuarios = view.findViewById<CardView>(R.id.cardUsuarios)

        val nombreAlmc = sharedPreferences.getString(nombreLocal, "")
        val apellidoPAlmc = sharedPreferences.getString(apellidoPLocal, "")
        val apellidoMAlmc = sharedPreferences.getString(apellidoMLocal, "")
        val rolAlmc = sharedPreferences.getString(rol, "")





        tvNombre.text = "$nombreAlmc $apellidoPAlmc $apellidoMAlmc"

        cardMensajeria.setOnClickListener {
            val intent = Intent(requireContext(), Mensajeria::class.java)
            startActivity(intent)
        }

        cardTareas.setOnClickListener {
            val intent = Intent(requireContext(), Tareas::class.java)
            startActivity(intent)
        }

        if (rolAlmc == "1") {
            cardUsuarios.visibility=View.VISIBLE
            cardUsuarios.setOnClickListener {
                val intent = Intent(requireContext(), Usuarios::class.java)
                startActivity(intent)
            }
        }else{
            cardUsuarios.visibility=View.GONE
        }


        return view
    }

    companion object {
        private const val PREFS_FILE_NAME = "MyPrefs"
    }

}