package me.amanjeet.daggertrack.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import me.amanjeet.daggertrack.model.LogMessage

class LogDiffUtilCallback : DiffUtil.ItemCallback<LogMessage>() {

    override fun areItemsTheSame(oldItem: LogMessage, newItem: LogMessage): Boolean {
        return oldItem.log == newItem.log
    }

    override fun areContentsTheSame(oldItem: LogMessage, newItem: LogMessage): Boolean {
        return oldItem == newItem
    }

}