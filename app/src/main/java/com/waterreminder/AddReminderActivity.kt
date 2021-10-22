package com.waterreminder

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.waterreminder.room.WaterDatabase
import kotlinx.android.synthetic.main.activity_add_reminder.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class AddReminderActivity : AppCompatActivity() {

    @Inject
    lateinit var database: WaterDatabase

    var time:Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reminder)

        et_time_text.isFocusable=false
        et_time_text.setOnClickListener{
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                time = cal.timeInMillis
                et_time_text.setText(SimpleDateFormat("HH:mm a").format(cal.time))
            }
            TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        btnReturnResult.setOnClickListener {
            val text = et_time_text.text.toString()
            if (text.isNotEmpty()) {
                val returnIntent = Intent()
                returnIntent.putExtra("result", text)
                returnIntent.putExtra("triggerDate", time)
                setResult(RESULT_OK, returnIntent)
                finish()
            }else{
                showToast("Please select time first.")
            }
        }
    }
}