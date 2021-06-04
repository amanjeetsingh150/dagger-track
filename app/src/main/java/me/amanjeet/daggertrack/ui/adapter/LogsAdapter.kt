package me.amanjeet.daggertrack.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import me.amanjeet.daggertrack.R
import me.amanjeet.daggertrack.model.LogMessage

class LogsAdapter : ListAdapter<LogMessage, LogsViewHolder>(LogDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.row_log_message,
            parent,
            false
        )
        return LogsViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogsViewHolder, position: Int) {
        holder.bindLogMessage(getItem(position))
    }
}