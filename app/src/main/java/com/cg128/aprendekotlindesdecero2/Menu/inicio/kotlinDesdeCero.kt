package com.cg128.aprendekotlindesdecero2.Menu.inicio

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cg128.aprendekotlindesdecero2.Menu.Maps.MainActivity
import com.cg128.aprendekotlindesdecero2.Menu.calculadora.CalculadoraActivity
import com.cg128.aprendekotlindesdecero2.Menu.caluladorDeMasa.masaCorporalActivity
import com.cg128.aprendekotlindesdecero2.Menu.notasDeActividades.appNotasActivity
import com.cg128.aprendekotlindesdecero2.R


class kotlinDesdeCero : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_kotlin_desde_cero)
        val btnAppMasaCorporal = findViewById<AppCompatButton>(R.id.appMasaCorporal)
        val btnappNotas = findViewById<AppCompatButton>(R.id.appNotas)
        val btnAppCalculator = findViewById<AppCompatButton>(R.id.appCalculator)
        val btnMaps = findViewById<AppCompatButton>(R.id.appMaps)

        btnAppMasaCorporal.setOnClickListener { navigeToAppMasaCorporal() }
        btnappNotas.setOnClickListener { navigeToAppNotes() }
        btnAppCalculator.setOnClickListener { navigeToAppCalculator() }
        btnMaps.setOnClickListener { navigeToAppMaps() }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun navigeToAppMasaCorporal(){
        val intent = Intent(this, masaCorporalActivity::class.java)
        startActivity(intent)
    }
    private fun kotlinDesdeCero.navigeToAppNotes() {
        val intent = Intent(this, appNotasActivity::class.java)
        startActivity(intent)
    }

    private fun kotlinDesdeCero.navigeToAppCalculator() {
        val intent = Intent(this, CalculadoraActivity::class.java)
        startActivity(intent)
    }

    private fun kotlinDesdeCero.navigeToAppMaps() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}


