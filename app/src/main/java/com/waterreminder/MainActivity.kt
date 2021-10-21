package com.waterreminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import androidx.lifecycle.Observer
import com.waterreminder.room.Db
import com.waterreminder.room.History
import com.waterreminder.room.Reminder
import com.waterreminder.room.WaterDatabase
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.appbar_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    
    lateinit var database: WaterDatabase
    private val sharedPrefFile = "kotlinSharedPreference"
    private lateinit var sharedPreferences: SharedPreferences

    var currentValue:Double = 0.0
    var percentage = "0"
    var totalForML = 1892.71
    var oneMl = 29.5735
    var totalForFlOz = 64
    var isFound = false
    var isLb = false
    var foundId = 0


    var ozList = arrayOf(8, 9, 12, 17, 20, 22, 24)
    var mlList = arrayOf(100, 250, 300, 500, 600, 750, 800)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = this.getSharedPreferences(
            sharedPrefFile,
            Context.MODE_PRIVATE
        )
//        lottie_view.progress=0.0f
        database = Db.database(applicationContext)
        database.historyDao().getAllHistory().observe(this, Observer {
            println(SimpleDateFormat("yyyy/MM/dd").format(Date()))
            if (it.isNotEmpty()) {
                val format = SimpleDateFormat("yyyy/MM/dd").format(Date())
                val format1 = SimpleDateFormat("yyyy/MM/dd").format(it[it.size - 1].date)
                if (format == format1) {
                    Log.d("1234", "${it[it.size - 1]}")
                    Log.d("1234", "${it[it.size - 1].id}")
                    isFound = true
                    foundId = it[it.size - 1].id
                    currentValue = it[it.size - 1].totalDrank
                    Log.d("1234", "$currentValue")
                    percentage = it[it.size - 1].percentage
                    lottie_view.progress = percentage.toFloat() / 100
                    percentageText.text = it[it.size - 1].percentage + "%"
                    if (!(sharedPreferences.getBoolean("isFlOz", true))) {
                        currentValueText.text = "${((currentValue * 29.5735).toFloat())}"
                    } else {
                        currentValueText.text = "${currentValue.toFloat()}"
                    }
                }
            }
        })
        addButton.setOnClickListener{
            if (sharedPreferences.getBoolean("isFlOz", true)){
                showOzDialogue()
            }else{
                showMlDialogue()
            }
        }

        val toggle = ActionBarDrawerToggle(
            this, drawyerLayout, toolbar, R.string.drawer_open, R.string.drawer_close
        )
        toggle.syncState()
        drawyerLayout.addDrawerListener(toggle)
        drawyerLayout.addDrawerListener(object : DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {
            }

            override fun onDrawerClosed(drawerView: View) {
            }

            override fun onDrawerStateChanged(newState: Int) {}
        })
        toggle.drawerArrowDrawable.color = Color.WHITE
        navigationView.setNavigationItemSelectedListener(this)
        checkReminderAlarms()
    }
    override fun onResume() {
        super.onResume()

        if (sharedPreferences.getBoolean("isFlOz", true)){
            percentageText.text = "$percentage%"
            currentValueText.text = "0"
            lottie_view.progress = percentage.toFloat()/100
            totalValueText.text = " / $totalForFlOz fl oz"
            if (isFound){
                currentValueText.text = "${currentValue.toFloat()}"
            }
        }else{
            percentageText.text = "$percentage%"
            currentValueText.text = "0"
            totalValueText.text = " / $totalForML ml"
            lottie_view.progress = percentage.toFloat()/100
            if(isFound){
                currentValueText.text = "${(currentValue*29.5735).toFloat()}"
            }
        }
    }
    private fun showMlDialogue(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Quantity")
        val stringList = mlList.map { it.toString()+" ml" }.toTypedArray()
        builder.setItems(stringList) { dialog, which ->
            when (which) {
                0 -> addToDatabase(convertMlToOz(mlList[0]))
                1 -> addToDatabase(convertMlToOz(mlList[1]))
                2 -> addToDatabase(convertMlToOz(mlList[2]))
                3 -> addToDatabase(convertMlToOz(mlList[3]))
                4 -> addToDatabase(convertMlToOz(mlList[4]))
                5 -> addToDatabase(convertMlToOz(mlList[5]))
                6 -> addToDatabase(convertMlToOz(mlList[6]))
                else -> throw IllegalArgumentException("Index Out of bound exception")
            }
        }
        // create and show the alert dialog
        val dialog = builder.create()
        dialog.show()
    }
    private fun showOzDialogue(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Quantity")
        val stringList = ozList.map { it.toString()+" fl oz" }.toTypedArray()
        builder.setItems(stringList) { dialog, which ->
            when (which) {
                0 -> addToDatabase(ozList[0] + currentValue)
                1 -> addToDatabase(ozList[1] + currentValue)
                2 -> addToDatabase(ozList[2] + currentValue)
                3 -> addToDatabase(ozList[3] + currentValue)
                4 -> addToDatabase(ozList[4] + currentValue)
                5 -> addToDatabase(ozList[5] + currentValue)
                6 -> addToDatabase(ozList[6] + currentValue)
                else -> throw IllegalArgumentException("Index Out of bound exception")
            }
        }
        // create and show the alert dialog
        val dialog = builder.create()
        dialog.show()
    }
    private fun addToDatabase(ozValue: Double){
        Log.d("123", "value of oz $ozValue")
        Log.d("123", "value of current $currentValue")
        var newOzValue = ozValue
        if (ozValue>64.0){
            newOzValue=64.0
        }
        Log.d("123", "value of current $newOzValue")
        val percentage = (newOzValue*100)/64
        val history = History(
            totalForFlOz, newOzValue, "${percentage.toInt()}",
            "fl oz", Date().time
        )
        GlobalScope.launch(Dispatchers.IO){
            if (isFound) {
                database.historyDao().insert(history.also {
                    it.id = foundId
                })
            }else{
                database.historyDao().insert(history)
            }
        }
    }
    private fun convertMlToOz(ml: Int):Double{
        val oz = ml*0.033814
        Log.d("12345", "Ml value $ml  oz value: $oz  current Value $currentValue")
        if (isFound){
            return currentValue+oz
        }else{
            currentValue = oz
            return  currentValue
        }
    }
//    private fun convertOzToMl(oz:Int):Double{
//        val ml = oz*29.5735
//        if (isFound){
//            val newMl = currentValue*29.5735
//            return newMl+ml
//        }else{
//            currentValue = oz
//            return  ml
//        }
//    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
        R.id.setting -> {
            val intent = Intent(this, SettingActivity::class.java)
            intent.putExtra("fromMain", true)
            startActivity(intent)
        }
        R.id.history -> startActivity(Intent(this, HistoryActivity::class.java))
        R.id.reminder -> startActivity(Intent(this, ReminderActivity::class.java))
        else -> { // Note the block
            print("x is neither 1 nor 2")
        }

    }
    return true
    }



    private fun checkReminderAlarms(){
//        database.reminderDao().getAllHistory().observeForever {list ->
//            Log.d("1122", "checkReminderAlarms: Observer called")
//            list.forEach {
//                if (it.isOn) {
//                    Log.d("1234", "Alarm is active $it")
//                    startAlarm(it.triggerDate, it.id)
//                } else {
//                    Log.d("1234", "Alarm is not active $it")
//                    cancelAlarm(it.id)
//                }
//            }
//        }
        database.reminderDao().getAllHistory().observe(this, { list ->
            list.forEach {
                if (it.isOn && !it.isAdded) {
                    Log.d("1234", "Alarm is active $it")
                    val id = 1000+it.id
                    startAlarm(it.triggerDate, id,it)
                } else if (!it.isOn) {
                    Log.d("1234", "Alarm is not active $it")
                    val id = 1000+it.id
                    cancelAlarm(id)
                }
            }
        })
    }
    private fun startAlarm(triggerTime: Long, intentId: Int,reminder: Reminder) {

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, intentId, intent, PendingIntent.FLAG_UPDATE_CURRENT)

//        val alarmUp = PendingIntent.getBroadcast(this, intentId,intent, PendingIntent.FLAG_NO_CREATE) != null

//        if (alarmUp) {
//            Log.d("1234", "Alarm is already active $intentId")
//        }else{
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        val remind = Reminder(
            reminder.triggerDate,reminder.title,true,true
        )
        GlobalScope.launch(Dispatchers.IO){
            database.reminderDao().insert(remind.also {
                it.id = reminder.id
            })
        }
//        }
    }
    private fun cancelAlarm(intentID: Int){

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, intentID, intent, 0)
//
//        val alarmUp = PendingIntent.getBroadcast(this, intentID, intent, PendingIntent.FLAG_NO_CREATE) != null
//
//        if (alarmUp) {
//            Log.d("1234", "Alarm is already active noot $intentID")
//        }else{
//            Log.d("123","Alarm is not active noot $intentID")
//        }
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()

    }

}