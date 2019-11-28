package bj4.dev.yhh.l.ui.activity.log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bj4.dev.yhh.l.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class LogActivityAdapter : RecyclerView.Adapter<LogRecyclerViewHolder>() {

    private val dateFormat = SimpleDateFormat("yyyy MM/dd", Locale.getDefault())

    val items = ArrayList<LogData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogRecyclerViewHolder {
        return LogRecyclerViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.viewholder_log,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: LogRecyclerViewHolder, position: Int) {
        holder.time.text = dateFormat.format(items[position].timeStamp)
        holder.message.text = items[position].data
    }
}

class LogRecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val time: TextView = view.findViewById(R.id.time)
    val message: TextView = view.findViewById(R.id.message)
}