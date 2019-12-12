package bj4.dev.yhh.l.ui.activity.main.fragment.list_table

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bj4.dev.yhh.l.R
import bj4.dev.yhh.repository.Constants
import bj4.dev.yhh.repository.LotteryType
import bj4.dev.yhh.repository.entity.*
import java.text.SimpleDateFormat
import java.util.*

class ListTableRecyclerViewAdapter(@LotteryType private val lotteryType: Int) :
    RecyclerView.Adapter<ListTableRecyclerViewHolder>() {

    companion object {
        private val dateFormatter = SimpleDateFormat("yyyy/MM/dd", Locale.TRADITIONAL_CHINESE)
    }

    val itemList = ArrayList<LotteryEntity>()

    var textSize: Float = 0f
    var cellWidth: Int = 0
    var cellDateWidth: Int = 0

    private fun getColumnCount(): Int = when (lotteryType) {
        LotteryType.LtoList3 -> Constants.LTO_LIST3_COUNT
        LotteryType.LtoList4 -> Constants.LTO_LIST4_COUNT
        else -> throw IllegalArgumentException("Wrong type")
    }

    private fun getNumberList(entity: LotteryEntity, lotteryType: Int): List<Int> {
        return when (lotteryType) {
            LotteryType.LtoList3 -> {
                ArrayList<Int>().also {
                    it.addAll((entity as LtoList3Entity).rawNormalNumbers)
                }
            }
            LotteryType.LtoList4 -> {
                ArrayList<Int>().also {
                    it.addAll((entity as LtoList4Entity).rawNormalNumbers)
                }
            }
            else -> throw IllegalArgumentException("unknown type")
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListTableRecyclerViewHolder {
        val container = LayoutInflater.from(parent.context).inflate(
            R.layout.epoxy_small_table,
            parent,
            false
        ) as LinearLayout
        val date =
            LayoutInflater.from(container.context)
                .inflate(R.layout.epoxy_small_cell_date, null, false)
                .also {
                    it.id = R.id.epoxy_cell_date
                }
        container.addView(
            date,
            LinearLayout.LayoutParams(
                cellDateWidth,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val columnCount = getColumnCount()

        for (index in 1..columnCount) {
            val cell =
                LayoutInflater.from(container.context)
                    .inflate(R.layout.epoxy_small_cell, null, false)
                    .also {
                        it.id = index
                    }
            container.addView(
                cell,
                LinearLayout.LayoutParams(cellWidth, LinearLayout.LayoutParams.WRAP_CONTENT)
            )
        }
        return ListTableRecyclerViewHolder(container)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: ListTableRecyclerViewHolder, position: Int) {
        val entity = itemList[position]

        holder.container.findViewById<TextView>(R.id.epoxy_cell_date).also { textView ->
            textView.text = dateFormatter.format(LotteryEntity.getTimeStamp(lotteryType, entity))
            (textView.layoutParams as LinearLayout.LayoutParams).width = cellDateWidth
            textView.textSize = textSize
            textView.setTextColor(textView.context.resources.getColor(R.color.large_table_date_text_foreground))
        }

        val column1Count = getColumnCount()
        val numberList = getNumberList(entity, lotteryType)

        for (index in 1..column1Count) {
            holder.container.findViewById<TextView>(index).also { textView ->
                val value = numberList[index - 1]
                textView.text = value.toString()
                textView.setTextColor(textView.resources.getColor(R.color.large_table_number_text_foreground))
                textView.textSize = textSize
                (textView.layoutParams as LinearLayout.LayoutParams).width = cellWidth
            }
        }
    }
}

class ListTableRecyclerViewHolder(val container: View) : RecyclerView.ViewHolder(container)