package bj4.dev.yhh.l.ui.activity.main.fragment.large_table

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bj4.dev.yhh.l.R
import bj4.dev.yhh.l.util.SharedPreferenceHelper
import bj4.dev.yhh.l.util.SortingMapUtil
import bj4.dev.yhh.repository.CellData
import bj4.dev.yhh.repository.Constants
import bj4.dev.yhh.repository.LotteryType
import bj4.dev.yhh.repository.entity.LotteryEntity
import bj4.dev.yhh.repository.entity.LtoBigEntity
import bj4.dev.yhh.repository.entity.LtoEntity
import bj4.dev.yhh.repository.entity.LtoHKEntity
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.subjects.PublishSubject
import java.text.SimpleDateFormat
import java.util.*

class LargeTableRecyclerViewAdapter(
    @LotteryType private val lotteryType: Int,
    private val isHeader: Boolean
) :
    RecyclerView.Adapter<LargeTableRecyclerViewHolder>() {
    companion object {
        private val dateFormatter = SimpleDateFormat("yyyy/MM/dd", Locale.TRADITIONAL_CHINESE)
        private val subTotalDateFormatter = SimpleDateFormat("yyyy/MM", Locale.TRADITIONAL_CHINESE)
    }

    val clickIntent = PublishSubject.create<Int>()

    val itemList = ArrayList<LotteryEntity>()
    var sortingType = SharedPreferenceHelper.DISPLAY_TYPE_ORDER
    var selectedIndex = -1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LargeTableRecyclerViewHolder {
        fun onCreateLtoHKViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): LargeTableRecyclerViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.epoxy_large_table,
                parent,
                false
            )
            val container = view.findViewById<LinearLayout>(R.id.container)
            val date =
                LayoutInflater.from(container.context)
                    .inflate(R.layout.epoxy_large_cell_date, null, false)
                    .also {
                        it.id = R.id.epoxy_cell_date
                    }
            container.addView(
                date,
                LinearLayout.LayoutParams(
                    parent.context.resources.getDimensionPixelSize(R.dimen.epoxy_large_table_date_cell_width),
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )

            val cellWidth =
                parent.context.resources.getDimensionPixelSize(R.dimen.epoxy_large_table_cell_width)

            for (index in Constants.LTO_HK_MIN..Constants.LTO_HK_MAX) {
                val cell =
                    LayoutInflater.from(container.context)
                        .inflate(R.layout.epoxy_large_cell, null, false)
                        .also {
                            it.id = index
                        }
                container.addView(
                    cell,
                    LinearLayout.LayoutParams(cellWidth, LinearLayout.LayoutParams.WRAP_CONTENT)
                )
            }

            return LargeTableRecyclerViewHolder(
                view
            )
        }

        fun onCreateLtoBigViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): LargeTableRecyclerViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.epoxy_large_table,
                parent,
                false
            )
            val container = view.findViewById<LinearLayout>(R.id.container)
            val date =
                LayoutInflater.from(container.context)
                    .inflate(R.layout.epoxy_large_cell_date, null, false)
                    .also {
                        it.id = R.id.epoxy_cell_date
                    }
            container.addView(
                date,
                LinearLayout.LayoutParams(
                    parent.context.resources.getDimensionPixelSize(R.dimen.epoxy_large_table_date_cell_width),
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )

            val cellWidth =
                parent.context.resources.getDimensionPixelSize(R.dimen.epoxy_large_table_cell_width)

            for (index in Constants.LTO_BIG_MIN..Constants.LTO_BIG_MAX) {
                val cell =
                    LayoutInflater.from(container.context)
                        .inflate(R.layout.epoxy_large_cell, null, false)
                        .also {
                            it.id = index
                        }
                container.addView(
                    cell,
                    LinearLayout.LayoutParams(cellWidth, LinearLayout.LayoutParams.WRAP_CONTENT)
                )
            }
            return LargeTableRecyclerViewHolder(
                view
            )
        }

        fun onCreateLtoViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): LargeTableRecyclerViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.epoxy_large_table,
                parent,
                false
            )
            val container = view.findViewById<LinearLayout>(R.id.container)
            val date =
                LayoutInflater.from(container.context)
                    .inflate(R.layout.epoxy_large_cell_date, null, false)
                    .also {
                        it.id = R.id.epoxy_cell_date
                    }
            container.addView(
                date,
                LinearLayout.LayoutParams(
                    parent.context.resources.getDimensionPixelSize(R.dimen.epoxy_large_table_date_cell_width),
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )

            val cellWidth =
                parent.context.resources.getDimensionPixelSize(R.dimen.epoxy_large_table_cell_width)

            for (index in Constants.LTO_COLUMN1_MIN..Constants.LTO_COLUMN1_MAX) {
                val cell =
                    LayoutInflater.from(container.context)
                        .inflate(R.layout.epoxy_large_cell, null, false)
                        .also {
                            it.id = index
                        }
                container.addView(
                    cell,
                    LinearLayout.LayoutParams(cellWidth, LinearLayout.LayoutParams.WRAP_CONTENT)
                )
            }
            for (index in Constants.LTO_COLUMN2_MIN..Constants.LTO_COLUMN2_MAX) {
                val cell =
                    LayoutInflater.from(container.context)
                        .inflate(R.layout.epoxy_large_cell, null, false)
                        .also {
                            it.id = Constants.LTO_COLUMN1_MAX + index
                        }
                container.addView(
                    cell,
                    LinearLayout.LayoutParams(cellWidth, LinearLayout.LayoutParams.WRAP_CONTENT)
                )
            }
            return LargeTableRecyclerViewHolder(
                view
            )
        }

        return when (lotteryType) {
            LotteryType.LtoHK -> onCreateLtoHKViewHolder(parent, viewType)
            LotteryType.LtoBig -> onCreateLtoBigViewHolder(parent, viewType)
            LotteryType.Lto -> onCreateLtoViewHolder(parent, viewType)
            else -> throw IllegalArgumentException("Wrong type")
        }
    }

    override fun getItemCount(): Int = if (isHeader) 1 else itemList.size

    private fun getTimeStamp(entity: LotteryEntity): Long {
        return when (lotteryType) {
            LotteryType.LtoBig -> (entity as LtoBigEntity).timeStamp
            LotteryType.Lto -> (entity as LtoEntity).timeStamp
            LotteryType.LtoHK -> (entity as LtoHKEntity).timeStamp
            else -> throw IllegalArgumentException("Unknown type")
        }
    }

    private fun isSubTotal(entity: LotteryEntity): Boolean {
        return when (lotteryType) {
            LotteryType.LtoBig -> (entity as LtoBigEntity).isSubTotal
            LotteryType.Lto -> (entity as LtoEntity).isSubTotal
            LotteryType.LtoHK -> (entity as LtoHKEntity).isSubTotal
            else -> throw IllegalArgumentException("Unknown type")
        }
    }

    private fun getColumn1(entity: LotteryEntity): List<CellData> {
        return when (lotteryType) {
            LotteryType.LtoBig -> (entity as LtoBigEntity).column1
            LotteryType.Lto -> (entity as LtoEntity).column1
            LotteryType.LtoHK -> (entity as LtoHKEntity).column1
            else -> throw IllegalArgumentException("Unknown type")
        }
    }

    private fun getColumn2(entity: LotteryEntity): List<CellData> {
        return when (lotteryType) {
            LotteryType.LtoBig -> throw IllegalStateException("Unsupported type")
            LotteryType.Lto -> (entity as LtoEntity).column2
            LotteryType.LtoHK -> throw IllegalStateException("Unsupported type")
            else -> throw IllegalArgumentException("Unknown type")
        }
    }

    override fun onBindViewHolder(holder: LargeTableRecyclerViewHolder, position: Int) {
        fun setClickListener(
            entity: LotteryEntity,
            holder: LargeTableRecyclerViewHolder,
            position: Int
        ) {
            if (!isSubTotal(entity)) {
                holder.container.clicks().doOnNext { clickIntent.onNext(position) }.subscribe()
            }
            if (selectedIndex == position) holder.foreground.setBackgroundColor(
                Color.argb(
                    0x10,
                    0x00,
                    0x85,
                    0x77
                )
            ) else {
                holder.foreground.setBackgroundColor(Color.TRANSPARENT)
            }
        }

        fun onBindLtoHKViewHolder(holder: LargeTableRecyclerViewHolder, position: Int) {
            fun getIndexBySortingType(index: Int, sortingType: Int): Int {
                return when (sortingType) {
                    SharedPreferenceHelper.DISPLAY_TYPE_ORDER -> index
                    SharedPreferenceHelper.DISPLAY_TYPE_END -> SortingMapUtil.sortingEndMap[index] + Constants.LTO_HK_MIN
                    SharedPreferenceHelper.DISPLAY_TYPE_COMBINATION -> SortingMapUtil.sortingCombinationMap[index] + Constants.LTO_HK_MIN
                    else -> index
                }
            }

            fun bindOrderHeader(holder: LargeTableRecyclerViewHolder) {
                holder.container.setBackgroundColor(
                    holder.container.context.resources.getColor(R.color.large_table_title_background)
                )

                holder.container.findViewById<TextView>(R.id.epoxy_cell_date).also { textView ->
                    textView.text = ""
                }
                for (index in Constants.LTO_HK_MIN..Constants.LTO_HK_MAX) {
                    holder.container.findViewById<TextView>(index).also { textView ->
                        val text = getIndexBySortingType(index, sortingType)
                        textView.text = String.format("%02d", text)
                        textView.setTextColor(textView.context.resources.getColor(R.color.large_table_date_text_foreground))
                    }
                }
            }

            fun bindOrderItem(holder: LargeTableRecyclerViewHolder) {
                val entity = itemList[position]
                setClickListener(entity, holder, position)
                if (isSubTotal(entity)) holder.container.setBackgroundColor(
                    holder.container.resources.getColor(
                        R.color.large_table_sub_total_background
                    )
                )
                else holder.container.setBackgroundColor(Color.TRANSPARENT)

                holder.container.findViewById<TextView>(R.id.epoxy_cell_date).also { textView ->
                    textView.text =
                        if (isSubTotal(entity)) subTotalDateFormatter.format(getTimeStamp(entity))
                        else dateFormatter.format(getTimeStamp(entity))

                    textView.setTextColor(textView.context.resources.getColor(R.color.large_table_date_text_foreground))
                }
                for (index in Constants.LTO_HK_MIN..Constants.LTO_HK_MAX) {
                    val item =
                        getColumn1(entity)[getIndexBySortingType(
                            index,
                            sortingType
                        ) - Constants.LTO_HK_MIN]

                    fun getCellBackground(item: CellData, sortingType: Int): Int {
                        return when (sortingType) {
                            SharedPreferenceHelper.DISPLAY_TYPE_ORDER -> {
                                if ((item.id % 10 != 9)) R.drawable.bg_cell else R.drawable.bg_cell_end
                            }
                            SharedPreferenceHelper.DISPLAY_TYPE_END -> {
                                if (item.id < 40) R.drawable.bg_cell else R.drawable.bg_cell_end
                            }
                            SharedPreferenceHelper.DISPLAY_TYPE_COMBINATION -> {
                                if (item.id < 40) R.drawable.bg_cell else R.drawable.bg_cell_end
                            }
                            else -> {
                                throw IllegalArgumentException("Unknonw sorting type")
                            }
                        }
                    }

                    holder.container.findViewById<TextView>(index).also { textView ->
                        textView.text = String.format("%02d", item.value)
                        val textColor: Int = when {
                            isSubTotal(entity) -> {
                                android.R.color.black
                            }
                            item.isNormalNumber -> {
                                R.color.large_table_number_text_foreground
                            }
                            item.isSpecialNumber -> {
                                R.color.large_table_special_number_text_foreground
                            }
                            else -> {
                                R.color.large_table_invisible_text_foreground
                            }
                        }
                        textView.setTextColor(textView.context.resources.getColor(textColor))
                        textView.setBackgroundResource(getCellBackground(item, sortingType))
                    }
                }
            }

            if (isHeader) bindOrderHeader(holder)
            else bindOrderItem(holder)
        }

        fun onBindLtoBigViewHolder(holder: LargeTableRecyclerViewHolder, position: Int) {
            fun getIndexBySortingType(index: Int, sortingType: Int): Int {
                return when (sortingType) {
                    SharedPreferenceHelper.DISPLAY_TYPE_ORDER -> index
                    SharedPreferenceHelper.DISPLAY_TYPE_END -> SortingMapUtil.sortingEndMap[index] + Constants.LTO_BIG_MIN
                    SharedPreferenceHelper.DISPLAY_TYPE_COMBINATION -> SortingMapUtil.sortingCombinationMap[index] + Constants.LTO_BIG_MIN
                    else -> index
                }
            }

            fun bindOrderHeader(holder: LargeTableRecyclerViewHolder) {
                holder.container.setBackgroundColor(
                    holder.container.context.resources.getColor(R.color.large_table_title_background)
                )

                holder.container.findViewById<TextView>(R.id.epoxy_cell_date).also { textView ->
                    textView.text = ""
                }
                for (index in Constants.LTO_BIG_MIN..Constants.LTO_BIG_MAX) {
                    holder.container.findViewById<TextView>(index).also { textView ->
                        val text = getIndexBySortingType(index, sortingType)
                        textView.text = String.format("%02d", text)
                        textView.setTextColor(textView.context.resources.getColor(R.color.large_table_date_text_foreground))
                    }
                }
            }

            fun bindOrderItem(holder: LargeTableRecyclerViewHolder) {
                val entity = itemList[position]
                setClickListener(entity, holder, position)
                if (isSubTotal(entity)) holder.container.setBackgroundColor(
                    holder.container.resources.getColor(
                        R.color.large_table_sub_total_background
                    )
                )
                else holder.container.setBackgroundColor(Color.TRANSPARENT)

                holder.container.findViewById<TextView>(R.id.epoxy_cell_date).also { textView ->
                    textView.text =
                        if (isSubTotal(entity)) subTotalDateFormatter.format(getTimeStamp(entity))
                        else dateFormatter.format(getTimeStamp(entity))

                    textView.setTextColor(textView.context.resources.getColor(R.color.large_table_date_text_foreground))
                }
                for (index in Constants.LTO_BIG_MIN..Constants.LTO_BIG_MAX) {
                    val item =
                        getColumn1(entity)[getIndexBySortingType(
                            index,
                            sortingType
                        ) - Constants.LTO_BIG_MIN]

                    fun getCellBackground(item: CellData, sortingType: Int): Int {
                        return when (sortingType) {
                            SharedPreferenceHelper.DISPLAY_TYPE_ORDER -> {
                                if ((item.id % 10 != 9)) R.drawable.bg_cell else R.drawable.bg_cell_end
                            }
                            SharedPreferenceHelper.DISPLAY_TYPE_END -> {
                                if (item.id < 40) R.drawable.bg_cell else R.drawable.bg_cell_end
                            }
                            SharedPreferenceHelper.DISPLAY_TYPE_COMBINATION -> {
                                if (item.id < 40) R.drawable.bg_cell else R.drawable.bg_cell_end
                            }
                            else -> {
                                throw IllegalArgumentException("Unknonw sorting type")
                            }
                        }
                    }

                    holder.container.findViewById<TextView>(index).also { textView ->
                        textView.text = String.format("%02d", item.value)
                        val textColor: Int = when {
                            isSubTotal(entity) -> {
                                android.R.color.black
                            }
                            item.isNormalNumber -> {
                                R.color.large_table_number_text_foreground
                            }
                            item.isSpecialNumber -> {
                                R.color.large_table_special_number_text_foreground
                            }
                            else -> {
                                R.color.large_table_invisible_text_foreground
                            }
                        }
                        textView.setTextColor(textView.context.resources.getColor(textColor))
                        textView.setBackgroundResource(getCellBackground(item, sortingType))
                    }
                }
            }

            if (isHeader) bindOrderHeader(holder)
            else bindOrderItem(holder)
        }

        fun onBindLtoViewHolder(holder: LargeTableRecyclerViewHolder, position: Int) {
            fun getIndexBySortingType(index: Int, sortingType: Int): Int {
                return when (sortingType) {
                    SharedPreferenceHelper.DISPLAY_TYPE_ORDER -> index
                    SharedPreferenceHelper.DISPLAY_TYPE_END -> SortingMapUtil.sortingEndMapOfLto[index] + Constants.LTO_COLUMN1_MIN
                    SharedPreferenceHelper.DISPLAY_TYPE_COMBINATION -> SortingMapUtil.sortingCombinationMapOfLto[index] + Constants.LTO_COLUMN1_MIN
                    else -> index
                }
            }

            fun bindOrderHeader(holder: LargeTableRecyclerViewHolder) {
                holder.container.setBackgroundColor(
                    holder.container.context.resources.getColor(R.color.large_table_title_background)
                )

                holder.container.findViewById<TextView>(R.id.epoxy_cell_date).also { textView ->
                    textView.text = ""
                }
                for (index in Constants.LTO_COLUMN1_MIN..Constants.LTO_COLUMN1_MAX) {
                    holder.container.findViewById<TextView>(index).also { textView ->
                        val text = getIndexBySortingType(index, sortingType)
                        textView.text = String.format("%02d", text)
                        textView.setTextColor(textView.context.resources.getColor(R.color.large_table_date_text_foreground))
                    }
                }
                // do not need to sort column 2
                for (index in Constants.LTO_COLUMN2_MIN..Constants.LTO_COLUMN2_MAX) {
                    holder.container.findViewById<TextView>(Constants.LTO_COLUMN1_MAX + index)
                        .also { textView ->
                            textView.text = String.format("%02d", index)
                            textView.setTextColor(textView.context.resources.getColor(R.color.large_table_date_text_foreground))
                        }
                }
            }

            fun bindOrderItem(holder: LargeTableRecyclerViewHolder) {
                val entity = itemList[position]
                setClickListener(entity, holder, position)
                if (isSubTotal(entity)) holder.container.setBackgroundColor(
                    holder.container.resources.getColor(
                        R.color.large_table_sub_total_background
                    )
                )
                else holder.container.setBackgroundColor(Color.TRANSPARENT)

                holder.container.findViewById<TextView>(R.id.epoxy_cell_date).also { textView ->
                    textView.text =
                        if (isSubTotal(entity)) subTotalDateFormatter.format(getTimeStamp(entity))
                        else dateFormatter.format(getTimeStamp(entity))

                    textView.setTextColor(textView.context.resources.getColor(R.color.large_table_date_text_foreground))
                }

                fun getCellBackground(item: CellData, sortingType: Int): Int {
                    return when (sortingType) {
                        SharedPreferenceHelper.DISPLAY_TYPE_ORDER -> {
                            if ((item.id % 10 != 9 && item.id != 38)) R.drawable.bg_cell else R.drawable.bg_cell_end
                        }
                        SharedPreferenceHelper.DISPLAY_TYPE_END -> {
                            if (item.id < 29) R.drawable.bg_cell else R.drawable.bg_cell_end
                        }
                        SharedPreferenceHelper.DISPLAY_TYPE_COMBINATION -> {
                            if (item.id < 28) R.drawable.bg_cell else R.drawable.bg_cell_end
                        }
                        else -> {
                            throw IllegalArgumentException("Unknonw sorting type")
                        }
                    }
                }

                for (index in Constants.LTO_COLUMN1_MIN..Constants.LTO_COLUMN1_MAX) {
                    val item =
                        getColumn1(entity)[getIndexBySortingType(
                            index,
                            sortingType
                        ) - Constants.LTO_COLUMN1_MIN]

                    holder.container.findViewById<TextView>(index).also { textView ->
                        textView.text = String.format("%02d", item.value)
                        val textColor: Int = when {
                            isSubTotal(entity) -> {
                                android.R.color.black
                            }
                            item.isNormalNumber -> {
                                R.color.large_table_number_text_foreground
                            }
                            item.isSpecialNumber -> {
                                R.color.large_table_special_number_text_foreground
                            }
                            else -> {
                                R.color.large_table_invisible_text_foreground
                            }
                        }
                        textView.setTextColor(textView.context.resources.getColor(textColor))
                        textView.setBackgroundResource(getCellBackground(item, sortingType))
                    }
                }

                for (index in Constants.LTO_COLUMN2_MIN..Constants.LTO_COLUMN2_MAX) {
                    val item = getColumn2(entity)[index - 1]

                    holder.container.findViewById<TextView>(Constants.LTO_COLUMN1_MAX + index)
                        .also { textView ->
                            textView.text = String.format("%02d", item.value)
                            val textColor: Int = when {
                                isSubTotal(entity) -> {
                                    android.R.color.black
                                }
                                item.isNormalNumber -> {
                                    R.color.large_table_number_text_foreground
                                }
                                item.isSpecialNumber -> {
                                    R.color.large_table_special_number_text_foreground
                                }
                                else -> {
                                    R.color.large_table_invisible_text_foreground
                                }
                            }
                            textView.setTextColor(textView.context.resources.getColor(textColor))
                            textView.setBackgroundResource(getCellBackground(item, sortingType))
                        }
                }
            }

            if (isHeader) bindOrderHeader(holder)
            else bindOrderItem(holder)
        }

        when (lotteryType) {
            LotteryType.LtoHK -> onBindLtoHKViewHolder(holder, position)
            LotteryType.LtoBig -> onBindLtoBigViewHolder(holder, position)
            LotteryType.Lto -> onBindLtoViewHolder(holder, position)
            else -> throw IllegalArgumentException("Wrong type")
        }
    }
}

class LargeTableRecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val container: LinearLayout = view.findViewById(R.id.container)
    val foreground: View = view.findViewById(R.id.foreground)
}