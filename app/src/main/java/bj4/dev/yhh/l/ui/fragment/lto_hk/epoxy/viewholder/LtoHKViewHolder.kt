package bj4.dev.yhh.l.ui.fragment.lto_hk.epoxy.viewholder

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import bj4.dev.yhh.l.R
import bj4.dev.yhh.l.external.epoxy.KotlinHolder
import bj4.dev.yhh.l.util.SharedPreferenceHelper
import bj4.dev.yhh.l.util.SortingMapUtil
import bj4.dev.yhh.repository.CellData
import bj4.dev.yhh.repository.Constants
import bj4.dev.yhh.repository.entity.LtoHKEntity
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import java.text.SimpleDateFormat
import java.util.*

@EpoxyModelClass(layout = R.layout.epoxy_large_table)
abstract class LtoHKViewHolder : EpoxyModelWithHolder<LtoHKViewHolder.Holder>() {

    companion object {
        private val dateFormatter = SimpleDateFormat("yyyy/MM/dd", Locale.TRADITIONAL_CHINESE)
        private val subTotalDateFormatter = SimpleDateFormat("yyyy/MM", Locale.TRADITIONAL_CHINESE)
    }

    @EpoxyAttribute
    lateinit var entity: LtoHKEntity

    @EpoxyAttribute
    var header: Boolean = false

    @EpoxyAttribute
    var sortingType: Int = SharedPreferenceHelper.DISPLAY_TYPE_ORDER

    override fun bind(holder: Holder) {
        super.bind(holder)

        fun getIndexBySortingType(index: Int, sortingType: Int): Int {
            return when (sortingType) {
                SharedPreferenceHelper.DISPLAY_TYPE_ORDER -> index
                SharedPreferenceHelper.DISPLAY_TYPE_END -> SortingMapUtil.sortingEndMap[index] + Constants.LTO_HK_MIN
                SharedPreferenceHelper.DISPLAY_TYPE_COMBINATION -> SortingMapUtil.sortingCombinationMap[index] + Constants.LTO_HK_MIN
                else -> index
            }
        }

        fun bindOrderHeader(holder: Holder) {
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

        fun bindOrderItem(holder: Holder) {
            if (entity.isSubTotal) holder.container.setBackgroundColor(
                holder.container.resources.getColor(
                    R.color.large_table_sub_total_background
                )
            )
            else holder.container.setBackgroundColor(Color.TRANSPARENT)

            holder.container.findViewById<TextView>(R.id.epoxy_cell_date).also { textView ->
                textView.text =
                    if (entity.isSubTotal) subTotalDateFormatter.format(entity.timeStamp)
                    else dateFormatter.format(entity.timeStamp)

                textView.setTextColor(textView.context.resources.getColor(R.color.large_table_date_text_foreground))
            }
            for (index in Constants.LTO_HK_MIN..Constants.LTO_HK_MAX) {
                val item =
                    entity.column1[getIndexBySortingType(index, sortingType) - Constants.LTO_HK_MIN]

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
                        entity.isSubTotal -> {
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

        if (header) bindOrderHeader(holder)
        else bindOrderItem(holder)
    }

    override fun buildView(parent: ViewGroup): View {
        val rtn = super.buildView(parent)
        val container = rtn.findViewById<LinearLayout>(R.id.container)
        val date =
            LayoutInflater.from(container.context).inflate(R.layout.epoxy_cell_date, null, false)
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
                LayoutInflater.from(container.context).inflate(R.layout.epoxy_cell, null, false)
                    .also {
                        it.id = index
                    }
            container.addView(
                cell,
                LinearLayout.LayoutParams(cellWidth, LinearLayout.LayoutParams.WRAP_CONTENT)
            )
        }
        return rtn
    }

    class Holder : KotlinHolder() {
        val container by bind<LinearLayout>(R.id.container)
    }
}