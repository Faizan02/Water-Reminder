package com.waterreminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.waterreminder.room.Db
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BootReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val database = Db.database(context.applicationContext)
        GlobalScope.launch(Dispatchers.IO) {
         val history = database.reminderDao().getAllHistoryOnce()
            history.forEach{
                if (it.isOn){
                    startAlarm(it.triggerDate,it.id,context)
                }
            }
        }
    }

    private fun startAlarm(triggerTime: Long, intentId: Int,context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, intentId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
}