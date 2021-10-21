package com.waterreminder

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_setting.*
import java.text.SimpleDateFormat
import java.util.*

class SettingActivity : AppCompatActivity() {
    private lateinit var btnFlOz: Button
    private lateinit var btnMl: Button
    private lateinit var btnLb: Button
    private lateinit var btnKg: Button
    private lateinit var lblWeight: TextView

    private var isFlOz:Boolean = true
    private var isLb:Boolean = true
    private val sharedPrefFile = "kotlinSharedPreference"
    private lateinit var sharedPreferences: SharedPreferences

    private var isFromMain = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        isFromMain = Intent().getBooleanExtra("fromMain",false)
        nextText.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        sharedPreferences = this.getSharedPreferences(sharedPrefFile,
            Context.MODE_PRIVATE)
        btnFlOz = findViewById(R.id.button)
        btnMl = findViewById(R.id.button2)
        btnLb = findViewById(R.id.button3)
        btnKg = findViewById(R.id.button4)
        lblWeight = findViewById(R.id.textView9)
        fetchPreviousValues()

        startTime.isFocusable=false
        startTime.setOnClickListener{
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                startTime.setText(SimpleDateFormat("hh:mm a").format(cal.time))
            }
            TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }
        endTime.isFocusable=false
        endTime.setOnClickListener{
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                endTime.setText(SimpleDateFormat("hh:mm a").format(cal.time))
            }
            TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }
        nextText.setOnClickListener{
            if (weightText.text.toString() == "0.0" || weightText.text.toString().isEmpty()){
                showToast("Weight Required")
            }else if (startTime.text.toString().isEmpty()){
                showToast("Start Time Required")
            }else if (endTime.text.toString().isEmpty()){
                showToast("End Time Required")
            }else{
                saveValue()
            }
        }


        btnFlOz.setOnClickListener{
            btnFlOz.setBackgroundResource(R.drawable.btn_selected)
            if (!isFlOz){
                btnMl.setBackgroundResource(R.drawable.btn1)
            }
            isFlOz=true
        }
        btnMl.setOnClickListener{
            btnMl.setBackgroundResource(R.drawable.btn_selected)
            if (isFlOz){
                btnFlOz.setBackgroundResource(R.drawable.btn1)
            }
            isFlOz=false
        }
        btnLb.setOnClickListener{
            btnLb.setBackgroundResource(R.drawable.btn_selected)
            lblWeight.text = btnLb.text
            if (!isLb){
                btnKg.setBackgroundResource(R.drawable.btn1)
            }
            isLb=true
        }
        btnKg.setOnClickListener{
            lblWeight.text = btnKg.text
            btnKg.setBackgroundResource(R.drawable.btn_selected)
            if (isLb){
                btnLb.setBackgroundResource(R.drawable.btn1)
            }
            isLb=false
        }

    }

    private fun fetchPreviousValues(){
        isLb = sharedPreferences.getBoolean("isLb",true)
        isFlOz = sharedPreferences.getBoolean("isFlOz",true)
        val weight = sharedPreferences.getFloat("weight",0.0F)
        weightText.setText(weight.toString())
        startTime.setText(sharedPreferences.getString("startTime",""))
        endTime.setText(sharedPreferences.getString("endTime",""))
        if (isLb){
            lblWeight.text = btnLb.text
            btnLb.setBackgroundResource(R.drawable.btn_selected)
            btnKg.setBackgroundResource(R.drawable.btn1)
        }else{
            lblWeight.text = btnKg.text
            btnLb.setBackgroundResource(R.drawable.btn1)
            btnKg.setBackgroundResource(R.drawable.btn_selected)
        }
        if (isFlOz){
            btnFlOz.setBackgroundResource(R.drawable.btn_selected)
            btnMl.setBackgroundResource(R.drawable.btn1)
        }else{
            btnFlOz.setBackgroundResource(R.drawable.btn1)
            btnMl.setBackgroundResource(R.drawable.btn_selected)
        }
    }
    private fun saveValue(){
        val editor:SharedPreferences.Editor =  sharedPreferences.edit()
        editor.putBoolean("isLb",isLb)
        editor.putBoolean("isFlOz",isFlOz)
        val weight = weightText.text.toString().toFloat()
        editor.putFloat("weight",weight)
        editor.putString("startTime",startTime.text.toString())
        editor.putString("endTime",endTime.text.toString())
        editor.apply()
        editor.commit()
        if (isFromMain){
            finish()
        }else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        finish()
    }
}