package bj4.dev.yhh.l.ui.activity.main.fragment.small_table

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bj4.dev.yhh.l.R
import bj4.dev.yhh.repository.Constants
import bj4.dev.yhh.repository.LotteryType
import bj4.dev.yhh.repository.entity.LotteryEntity
import bj4.dev.yhh.repository.entity.LtoBigEntity
import bj4.dev.yhh.repository.entity.LtoEntity
import bj4.dev.yhh.repository.entity.LtoHKEntity
import java.text.SimpleDateFormat
import java.util.*

class SmallTableRecyclerViewAdapter(@LotteryType private val lotteryType: Int) :
    RecyclerView.Adapter<SmallTableRecyclerViewHolder>() {
    companion object {
        private val dateFormatter = SimpleDateFormat("yyyy/MM/dd", Locale.TRADITIONAL_CHINESE)
    }

    val itemList = ArrayList<LotteryEntity>()
    var diffValue: Int = 0

    private fun getTimeStamp(entity: LotteryEntity, lotteryType: Int): Long {
        return when (lotteryType) {
            LotteryType.LtoHK -> {
                (entity as LtoHKEntity).timeStamp
            }
            LotteryType.Lto -> {
                (entity as LtoEntity).timeStamp
            }
            LotteryType.LtoBig -> {
                (entity as LtoBigEntity).timeStamp
            }
            else -> throw IllegalArgumentException("unknown type")
        }
    }

    private fun getColumn1Max(): Int = when (lotteryType) {
        LotteryType.LtoBig -> Constants.LTO_BIG_MAX
        LotteryType.Lto -> Constants.LTO_COLUMN1_MAX
        LotteryType.LtoHK -> Constants.LTO_HK_MAX
        else -> throw IllegalArgumentException("Wrong type")
    }

    private fun getNumberList(entity: LotteryEntity, lotteryType: Int): List<Int> {
        return when (lotteryType) {
            LotteryType.LtoHK -> {
                ArrayList<Int>().also {
                    it.addAll((entity as LtoHKEntity).rawNormalNumbers)
                    it.addAll(entity.rawSpecialNumbers)
                    it.sort()
                }
            }
            LotteryType.Lto -> {
                ArrayList<Int>().also {
                    it.addAll((entity as LtoEntity).rawNormalNumbers)
                    it.sort()
                }
            }
            LotteryType.LtoBig -> {
                ArrayList<Int>().also {
                    it.addAll((entity as LtoBigEntity).rawNormalNumbers)
                    it.addAll(entity.rawSpecialNumbers)
                    it.sort()
                }
            }
            else -> throw IllegalArgumentException("unknown type")
        }
    }

    private fun getColumn1Count(): Int = when (lotteryType) {
        LotteryType.LtoBig -> Constants.LTO_BIG_NUMBER_COUNT
        LotteryType.Lto -> Constants.LTO_COLUMN1_COUNT
        LotteryType.LtoHK -> Constants.LTO_HK_NUMBER_COUNT
        else -> throw IllegalArgumentException("Wrong type")
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SmallTableRecyclerViewHolder {
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
                parent.context.resources.getDimensionPixelSize(R.dimen.epoxy_small_table_date_cell_width),
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val cellWidth =
            parent.context.resources.getDimensionPixelSize(R.dimen.epoxy_small_table_cell_width)

        val column1Count = getColumn1Count()

        for (index in 1..column1Count) {
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
        return SmallTableRecyclerViewHolder(container)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: SmallTableRecyclerViewHolder, position: Int) {
        val entity = itemList[position]
        val diffEntity = if (position == 0) null else itemList[position - 1]

        holder.container.findViewById<TextView>(R.id.epoxy_cell_date).also { textView ->
            textView.text = dateFormatter.format(getTimeStamp(entity, lotteryType))

            textView.setTextColor(textView.context.resources.getColor(R.color.large_table_date_text_foreground))
        }

        val column1Count = getColumn1Count()
        val numberList = getNumberList(entity, lotteryType)
        val diffNumberList =
            if (diffEntity == null) ArrayList() else getNumberList(diffEntity!!, lotteryType)
        val column1Max = getColumn1Max()

        fun hit(value: Int, diffValue: Int, diffNumberList: List<Int>, maxValue: Int): Boolean {
            diffNumberList.forEach {
                if (((it + diffValue + maxValue) % maxValue) == value)
                    return true
            }
            return false
        }

        for (index in 1..column1Count) {
            holder.container.findViewById<TextView>(index).also { textView ->
                val value = numberList[index - 1]
                textView.text = value.toString()
                textView.setTextColor(
                    if (!hit(
                            value,
                            diffValue,
                            diffNumberList,
                            column1Max
                        )
                    ) textView.resources.getColor(R.color.large_table_number_text_foreground)
                    else textView.resources.getColor(R.color.large_table_special_number_text_foreground)
                )
            }
        }
    }
}

class SmallTableRecyclerViewHolder(val container: View) : RecyclerView.ViewHolder(container)