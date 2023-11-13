package com.razen.laborbuddy.inicio

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.razen.laborbuddy.secciones.cuenta.CuentaFragment
import com.razen.laborbuddy.secciones.jornada.JornadaFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.razen.laborbuddy.R
import com.razen.laborbuddy.secciones.panel.PanelFragment


@Suppress("DEPRECATION")
class PantallaPrincipal : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_principal)

        val jornadaFragment = JornadaFragment()
        val cuentaFragment = CuentaFragment()
        val panelFragment = PanelFragment()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.itemFichaje ->{
                    fragmentoActual(jornadaFragment)
                    true
                }
                R.id.itemInbox ->{
                    fragmentoActual(panelFragment)
                    true
                }
                R.id.itemCuenta ->{
                    fragmentoActual(cuentaFragment)
                    true
                }
                else -> false
            }
        }


    }

    private fun fragmentoActual (fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.contenedor, fragment)
            commit()
        }
    }


}