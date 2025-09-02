package com.cg128.aprendekotlindesdecero2.Menu.caluladorDeMasa

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cg128.aprendekotlindesdecero2.R

class ResultIMCActivity : AppCompatActivity() {

    private lateinit var tvResult: TextView
    private lateinit var tvIMC: TextView
    private lateinit var tvDescription: TextView
    private lateinit var btnRecalcular: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_result_imcactivity)
        val result = intent.extras?.getDouble("IMC_RESULT")?: -1.0
        initComponents()
        initUI(result)
        initListeners()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initListeners() {
        btnRecalcular.setOnClickListener { onBackPressed() }
    }

    private fun initUI(result:Double) {
        tvIMC.text = result.toString()
        when(result){
            in 0.00..18.50 ->{ //bajo peso

                tvResult.text = getString(R.string.titleBajoPeso)
                tvResult.setTextColor(ContextCompat.getColor(this, R.color.bajoDePeso))
                tvDescription.text = getString(R.string.descriptionBajoPeso)
            }
            in 18.51..24.99 ->{ //peso normal

                tvResult.text = getString(R.string.titlePesoNormal)
                tvResult.setTextColor(ContextCompat.getColor(this, R.color.normal))
                tvDescription.text = getString(R.string.descriptionPesoNormal)
            }
            in 23.00..29.99 ->{  //Sobrepeso

                tvResult.text = getString(R.string.titleSobrepeso)
                tvResult.setTextColor(ContextCompat.getColor(this, R.color.sobrepeso))

                tvDescription.text = getString(R.string.descriptionSobrepeso)
            }
            in 30.00..99.00 ->{  //obesidad

                tvResult.text = getString(R.string.titleObesidad)
                tvResult.setTextColor(ContextCompat.getColor(this, R.color.obesidad))
                tvDescription.text = getString(R.string.descriptionObesidad)
            }
            else ->{ //Error
                tvIMC.text = getString(R.string.error)
                tvResult.text = getString(R.string.error)
                tvResult.setTextColor(ContextCompat.getColor(this, R.color.obesidad))

                tvDescription.text = getString(R.string.error)
            }
        }
    }

    private fun initComponents() {
        tvIMC = findViewById(R.id.tvIMC)
        tvResult = findViewById(R.id.tvResult)
        tvDescription = findViewById(R.id.tvDescription)
        btnRecalcular = findViewById(R.id.btnRecalcular)
    }
}