package com.waterreminder.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.waterreminder.R
import com.waterreminder.room.Reminder
import java.text.SimpleDateFormat

class ReminderRecyclerviewAdapter(
    private val list: List<Reminder>,
    private val onCheckedCallback: (Boolean, Int, Reminder) -> Unit
): RecyclerView.Adapter<ReminderRecyclerviewAdapter.MainViewHolder>()  {

    class MainViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var timeText: TextView = itemView.findViewById(R.id.time_textview)
        var onSwitch: SwitchCompat = itemView.findViewById(R.id.reminder_on_switch)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_reminder, parent, false)
        return MainViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        var parent:Reminder=list[position]
        holder.timeText.text = SimpleDateFormat("hh:mm a").format(parent.triggerDate)

        holder.onSwitch.isChecked = parent.isOn
        holder.onSwitch.setOnCheckedChangeListener { _, isChecked ->
            onCheckedCallback(isChecked, position, list[position])
        }
       
    }

    override fun getItemCount(): Int {
        return list.size
    }
}