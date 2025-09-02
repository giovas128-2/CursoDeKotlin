package com.cg128.aprendekotlindesdecero2.Menu.caluladorDeMasa

import android.content.Intent
import android.icu.text.DecimalFormat
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cg128.aprendekotlindesdecero2.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.slider.RangeSlider


private var isMaleSelected: Boolean = true
private  var isFemaleSelected: Boolean = false
private var currentWeight:Int = 60
private var currentAge:Int = 20
private var currentHeight:Int =120
private lateinit var viewMale: CardView
private lateinit var viewFemale: CardView
private lateinit var tvHeight: TextView
private lateinit var rsHeight: RangeSlider
private lateinit var btnPlusWeight: FloatingActionButton
private lateinit var btnsubtractWeight: FloatingActionButton
private lateinit var tvPeso: TextView
private lateinit var btnsubtractAge : FloatingActionButton
private lateinit var btnPlusAge : FloatingActionButton
private lateinit var tvEdad : TextView
private lateinit var btnCalcular : Button


class masaCorporalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_masa_corporal)
        initComponent()
        initListener()
        initUI()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }




    private fun initComponent(){
        viewMale = findViewById(R.id.viewMale)
        viewFemale = findViewById(R.id.viewFemale)
        tvHeight = findViewById(R.id.tvHeight)
        rsHeight = findViewById(R.id.rsHeight)
        btnsubtractWeight = findViewById(R.id.btnsubtractWeight)
        btnPlusWeight = findViewById(R.id.btnPlusWeight)
        tvPeso = findViewById(R.id.tvPeso)
        btnsubtractAge = findViewById(R.id.btnsubtractAge)
        btnPlusAge = findViewById(R.id.btnPlusAge)
        tvEdad = findViewById(R.id.tvEdad)
        btnCalcular = findViewById(R.id.btnCalcular)
    }

    private fun initListener(){
        viewMale.setOnClickListener {
            changeGender()
            setGenderColor() }
        viewFemale.setOnClickListener {
            changeGender()
            setGenderColor() }

        rsHeight.addOnChangeListener{ _, value, _ ->
            val df = DecimalFormat("#.##")
            currentHeight =  df.format(value).toInt()
            tvHeight.text = "$currentHeight cm"
        }
        btnPlusWeight.setOnClickListener {
            currentWeight += 1
            setWeight()
        }
        btnsubtractWeight.setOnClickListener {
            currentWeight -= 1
            setWeight()

        }
        btnPlusAge.setOnClickListener {
            currentAge += 1
            setAge()
        }
        btnsubtractAge.setOnClickListener {
            currentAge -= 1
            setAge()
        }
        btnCalcular.setOnClickListener {
            val result = calculateIMC()
            navegateToResult(result)
        }
    }

    private fun navegateToResult(result: Double) {
        val intent = Intent(this, ResultIMCActivity::class.java)
        intent.putExtra("IMC_RESULT", result)
        startActivity(intent)
    }

    private fun calculateIMC(): Double {
        val df = DecimalFormat("#.##")
        val imc = currentWeight / (currentHeight.toDouble() / 100 * currentHeight.toDouble() / 100)
        return df.format(imc).toDouble()

    }

    private fun setAge(){
        tvEdad.text = currentAge.toString()
    }
    private fun setWeight(){
        tvPeso.text = currentWeight.toString()
    }
    private fun changeGender(){
        isMaleSelected = !isMaleSelected
        isFemaleSelected = !isFemaleSelected

    }
    private fun setGenderColor(){
        viewMale.setCardBackgroundColor(getBackgroundColor(isMaleSelected))
        viewFemale.setCardBackgroundColor(getBackgroundColor(isFemaleSelected))
    }

    private fun getBackgroundColor(isSelectComponent:Boolean): Int{

        val colorReference = if(isSelectComponent){
            R.color.background_component_selected
        }else{
            R.color.background_component
        }

        return ContextCompat.getColor(this, colorReference)
    }

    private fun initUI() {
        setGenderColor()
        setWeight()
        setAge()
    }
}