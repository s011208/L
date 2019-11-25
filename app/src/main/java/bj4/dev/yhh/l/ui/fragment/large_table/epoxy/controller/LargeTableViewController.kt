package bj4.dev.yhh.l.ui.fragment.large_table.epoxy.controller

import bj4.dev.yhh.l.ui.fragment.large_table.lto.epoxy.viewholder.ltoViewHolder
import bj4.dev.yhh.l.ui.fragment.large_table.lto_big.epoxy.viewholder.ltoBigViewHolder
import bj4.dev.yhh.l.ui.fragment.large_table.lto_hk.epoxy.viewholder.ltoHKViewHolder
import bj4.dev.yhh.repository.LotteryType
import bj4.dev.yhh.repository.entity.LotteryEntity
import bj4.dev.yhh.repository.entity.LtoBigEntity
import bj4.dev.yhh.repository.entity.LtoEntity
import bj4.dev.yhh.repository.entity.LtoHKEntity
import com.airbnb.epoxy.Typed2EpoxyController
import com.airbnb.epoxy.Typed3EpoxyController

class LargeTableViewController :
    Typed3EpoxyController<List<LotteryEntity>, Int, Int>() {
    override fun buildModels(data: List<LotteryEntity>, sortingType: Int, ltoType: Int) {
        data.forEach {
            when (ltoType) {
                LotteryType.LtoHK -> {
                    val item = it as LtoHKEntity
                    ltoHKViewHolder {
                        id("$sortingType$item")
                        entity(item)
                        sortingType(sortingType)
                    }
                }
                LotteryType.LtoBig -> {
                    val item = it as LtoBigEntity
                    ltoBigViewHolder {
                        id("$sortingType$item")
                        entity(item)
                        sortingType(sortingType)
                    }
                }
                LotteryType.Lto -> {
                    val item = it as LtoEntity
                    ltoViewHolder {
                        id("$sortingType$item")
                        entity(item)
                        sortingType(sortingType)
                    }
                }
            }
        }
    }
}