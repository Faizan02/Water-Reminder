package com.waterreminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.waterreminder.room.Db
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        val database = Db.database(context.applicationContext)
        GlobalScope.launch(Dispatchers.IO) {
            val history = database.historyDao().getLastHistory()
            if (history != null) {
                if (SimpleDateFormat("yyyy/MM/dd").format(Date()) ==
                    SimpleDateFormat("yyyy/MM/dd").format(history.date)
                ) {
                    //found today's data
                    //chk if today's task is completed
                    if (history.totalDrank < 64) {
                        val notificationUtils = NotificationUtils(context)
                        val notification = notificationUtils.getNotificationBuilder(
                            "It's time to drink water",
                            "You still have ${64 - history.totalDrank} fl oz to drink for Today."
                        ).build()
                        notificationUtils.getManager().notify(150, notification)
                    }
                } else {
                    //Today's data not found
                    val notificationUtils = NotificationUtils(context)
                    val notification = notificationUtils.getNotificationBuilder(
                        "It's time to drink water",
                        "It looks like you haven't had any water Today."
                    ).build()
                    notificationUtils.getManager().notify(150, notification)
                }
            }else{
                val notificationUtils = NotificationUtils(context)
                val notification = notificationUtils.getNotificationBuilder(
                    "It's time to drink water",
                    "It looks like you haven't had any water Today."
                ).build()
                notificationUtils.getManager().notify(150, notification)
            }
        }

    }


}