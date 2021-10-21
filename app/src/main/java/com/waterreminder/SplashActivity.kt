package com.waterreminder

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.waterreminder.room.Db
import com.waterreminder.room.Reminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash)
        Handler(Looper.getMainLooper()).postDelayed(
            Runnable {
                val sharedPrefFile = "kotlinSharedPreference"
                var sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,
                    Context.MODE_PRIVATE)
                val weight = sharedPreferences.getFloat("weight",0.0f)
                val database = Db.database(applicationContext)
                database.reminderDao().getAllHistory().observe(this,{
                    if (it.isEmpty()){
                        val calendar = Calendar.getInstance();
                        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                                11, 0, 0)
                        val reminder1 = Reminder(calendar.timeInMillis,"Water Reminder",true,false)
                        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                                12, 0, 0)
                        val reminder2 = Reminder(calendar.timeInMillis,"Water Reminder",true,false)
                        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                                13, 0, 0)
                        val reminder3 = Reminder(calendar.timeInMillis,"Water Reminder",true,false)
                        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                                14, 0, 0)
                        val reminder4 = Reminder(calendar.timeInMillis,"Water Reminder",true,false)
                        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                                15, 0, 0)
                        val reminder5 = Reminder(calendar.timeInMillis,"Water Reminder",true,false)
                        GlobalScope.launch(Dispatchers.IO){
                            database.reminderDao().insert(reminder1)
                            database.reminderDao().insert(reminder2)
                            database.reminderDao().insert(reminder3)
                            database.reminderDao().insert(reminder4)
                            database.reminderDao().insert(reminder5)
                        }
                    }
                })
                if (weight != 0.0f){
                    startActivity(Intent(this@SplashActivity,MainActivity::class.java))
                    finish()
                }else{
                    val intent = Intent(this@SplashActivity,SettingActivity::class.java)
                    intent.putExtra("fromMain", true)
                    startActivity(intent)
                    finish()
                }
            },
            1500
        )
    }
}

fun Context.showToast(msg: String, length: Int = Toast.LENGTH_SHORT){
    Toast.makeText(this, msg, length).show()
}