package com.waterreminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.waterreminder.adapter.ReminderRecyclerviewAdapter
import com.waterreminder.room.Reminder
import com.waterreminder.room.WaterDatabase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_reminder.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReminderActivity : AppCompatActivity() {

    private lateinit var activityAdapter: ReminderRecyclerviewAdapter
    private var list = mutableListOf<Reminder>()

    @Inject
    lateinit var database: WaterDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        fabAddReminder.setOnClickListener {
            startActivityForResult(
                Intent(
                    this,
                    AddReminderActivity::class.java
                ),
                1
            )
        }
        activityAdapter = ReminderRecyclerviewAdapter(list) { isChecked, position, reminder ->

            val remind = Reminder(
                reminder.triggerDate,reminder.title,isChecked,false
            )
            showToast("this is test $position $isChecked")
            GlobalScope.launch(Dispatchers.IO){
                database.reminderDao().insert(remind.also {
                    it.id = reminder.id
                })
            }
            if (isChecked){
                startAlarm(reminder.triggerDate,reminder.id,remind)
            }else{
                cancelAlarm(reminder.id)
            }
        }
        val manager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        reminder_recyclerview.layoutManager = manager

        reminder_recyclerview.adapter = activityAdapter
        activityAdapter.notifyDataSetChanged()
        fetchReminderData()
    }

    private fun fetchReminderData() {
        database.reminderDao().getAllHistory().observe(this, {
            list.clear()
            list.addAll(it)
            activityAdapter.notifyDataSetChanged()
        })
    }
    private fun startAlarm(triggerTime: Long, intentId: Int,reminder: Reminder) {

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, intentId, intent, PendingIntent.FLAG_UPDATE_CURRENT)

//        val alarmUp = PendingIntent.getBroadcast(this, intentId, intent, PendingIntent.FLAG_NO_CREATE) != null
//
//        if (alarmUp) {
//            Log.d("1234", "Alarm is already active $intentId")
//        }else{
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )

//        }
        val remind = Reminder(
            reminder.triggerDate,reminder.title,true,true
        )
        GlobalScope.launch(Dispatchers.IO){
            database.reminderDao().insert(remind.also {
                it.id = reminder.id
            })
        }
    }
    private fun cancelAlarm(intentID: Int){
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, intentID, intent, 0)
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode  == 1 && resultCode == RESULT_OK && data != null) {
            val triggerDate = data.getLongExtra("triggerDate",0)
            if (triggerDate != 0L){
                val reminder1 = Reminder(triggerDate,"Water Reminder",true,false)
                GlobalScope.launch(Dispatchers.IO) {
                    database.reminderDao().insert(reminder1)
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }
}