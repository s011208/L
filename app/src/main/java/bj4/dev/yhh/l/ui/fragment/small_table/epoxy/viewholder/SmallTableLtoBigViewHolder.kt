package bj4.dev.yhh.l.ui.fragment.small_table.epoxy.viewholder

import bj4.dev.yhh.l.R
import bj4.dev.yhh.repository.LotteryType
import com.airbnb.epoxy.EpoxyModelClass

@EpoxyModelClass(layout = R.layout.epoxy_small_table)
abstract class SmallTableLtoBigViewHolder : SmallTableViewHolder() {
    override fun getLotteryType(): Int = LotteryType.LtoBig
}