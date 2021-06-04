package me.amanjeet.daggertrack.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_log_message.view.*
import me.amanjeet.daggertrack.model.LogMessage

class LogsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindLogMessage(item: LogMessage) {
        itemView.logMessageTextView.text = item.log
    }

}