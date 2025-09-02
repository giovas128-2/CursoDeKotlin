package com.cg128.aprendekotlindesdecero2.Menu.calculadora

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cg128.aprendekotlindesdecero2.R
import com.google.android.material.button.MaterialButton
import net.objecthunter.exp4j.ExpressionBuilder
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale


class CalculadoraActivity : AppCompatActivity() {

    private lateinit var tvResult: EditText
    private var expresion: String = ""
    private var openParenthesis = 0
    private val decimalFormat = DecimalFormat("#,###.########", DecimalFormatSymbols(Locale.US))



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calculadora)

        tvResult = findViewById(R.id.tvResult)

        tvResult.showSoftInputOnFocus = false // evita que aparezca el teclado al tocar
        tvResult.isCursorVisible = true       // opcional: mostrar cursor

        // Números
        val numeros = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3,
            R.id.btn4, R.id.btn5, R.id.btn6,
            R.id.btn7, R.id.btn8, R.id.btn9
        )

        for (id in numeros) {
            findViewById<MaterialButton>(id).setOnClickListener {
                val valor = (it as MaterialButton).text.toString()
                agregarAExpresion(valor)
            }
        }

        // Operadores
        val operadores = listOf(
            R.id.btnSumar, R.id.btnResta, R.id.btnMultiplicar,
            R.id.btnDividir, R.id.btnPunto, R.id.btnPorcentaje
        )

        for (id in operadores) {
            findViewById<MaterialButton>(id).setOnClickListener {
                val valor = (it as MaterialButton).text.toString()
                agregarAExpresion(valor)
            }
        }

        // Botón C → limpiar todo
        findViewById<MaterialButton>(R.id.btndelete).setOnClickListener {
            expresion = ""
            tvResult.setText("")
        }

        // Botón D → borrar un número
        findViewById<MaterialButton>(R.id.btnDeleteNum).setOnClickListener {
            borrarEnCursor()
        }

        // Botón =
        findViewById<MaterialButton>(R.id.btnResult).setOnClickListener {
            try {
                // Antes de evaluar, cerramos todos los paréntesis abiertos
                var reemplazo = expresion.replace(",", "").replace("X", "*").replace("%", "/100.0")
                repeat(openParenthesis) { reemplazo += ")" }

                val resultado = ExpressionBuilder(reemplazo).build().evaluate()
                val resultadoStr = resultado.toString()
                val partes = resultadoStr.split(".")
                val parteEntera = decimalFormat.format(partes[0].toLong())
                val parteDecimal = partes.getOrNull(1)
                val mostrado = if (parteDecimal != null && parteDecimal != "0") {
                    "$parteEntera.$parteDecimal"
                } else {
                    parteEntera
                }

                tvResult.setText(mostrado)
                expresion = mostrado.replace(",", "")
                openParenthesis = 0 // 👈 reinicia contador de paréntesis
            } catch (e: Exception) {
                Toast.makeText(this, "Expresión inválida", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<MaterialButton>(R.id.btnParenthesis).setOnClickListener {
            if (expresion.isEmpty() || expresion.last().toString() in "+-*/(") {
                // Si es inicio o después de un operador → abrir
                agregarAExpresion("(")
                openParenthesis++
            } else if (openParenthesis > 0) {
                // Si ya hay paréntesis abiertos → cerrar
                agregarAExpresion(")")
                openParenthesis--
            } else {
                // Si no hay abiertos → abrir uno nuevo
                agregarAExpresion("(")
                openParenthesis++
            }
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    private fun formatearExpresion(expr: String): String {
        return try {
            val normalizado = expr.replace(",", "")
            if (normalizado.isEmpty()) return ""

            if (normalizado.contains(".")) {
                val partes = normalizado.split(".")
                val parteEntera = partes[0]
                val parteDecimal = partes.getOrNull(1) ?: ""
                decimalFormat.format(parteEntera.toLong()) +
                        if (parteDecimal.isNotEmpty()) ".$parteDecimal" else ""
            } else {
                decimalFormat.format(normalizado.toLong())
            }
        } catch (e: Exception) {
            expr // si no se puede formatear, devolvemos la expresión tal cual
        }
    }

    private fun agregarAExpresion(valor: String) {
        val cursorPos = tvResult.selectionStart

        // Insertamos el valor en la posición del cursor
        expresion = expresion.substring(0, cursorPos) + valor + expresion.substring(cursorPos)

        try {
            // Si el valor es un operador o hay un punto decimal, no formateamos
            if (valor in "+-*/%()" || expresion.contains(".")) {
                tvResult.setText(expresion)
            } else {
                // Solo formateamos números enteros válidos
                val hastaCursor = expresion.substring(0, cursorPos + valor.length).replace(",", "")
                val numero = hastaCursor.toLongOrNull()
                if (numero != null) {
                    val formateado = decimalFormat.format(numero)
                    val resto = expresion.substring(cursorPos + valor.length)
                    tvResult.setText(formateado + resto)
                } else {
                    tvResult.setText(expresion) // si no es número válido, mostramos tal cual
                }
            }
        } catch (e: Exception) {
            // Cualquier error, simplemente mostramos la expresión
            tvResult.setText(expresion)
        }

        // Movemos el cursor justo después del valor agregado
        tvResult.setSelection(cursorPos + valor.length)
    }


    private fun borrarEnCursor() {
        val cursorPos = tvResult.selectionStart
        if (cursorPos > 0) {
            expresion = expresion.removeRange(cursorPos - 1, cursorPos)
            tvResult.setText(expresion)
            tvResult.setSelection(cursorPos - 1) // movemos el cursor atrás
        }
    }

}