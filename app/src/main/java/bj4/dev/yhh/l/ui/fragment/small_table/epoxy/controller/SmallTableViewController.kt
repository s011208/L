package bj4.dev.yhh.l.ui.fragment.small_table.epoxy.controller

import bj4.dev.yhh.l.ui.fragment.small_table.epoxy.viewholder.smallTableLtoBigViewHolder
import bj4.dev.yhh.l.ui.fragment.small_table.epoxy.viewholder.smallTableLtoHKViewHolder
import bj4.dev.yhh.l.ui.fragment.small_table.epoxy.viewholder.smallTableLtoViewHolder
import bj4.dev.yhh.repository.LotteryType
import bj4.dev.yhh.repository.entity.LotteryEntity
import bj4.dev.yhh.repository.entity.LtoBigEntity
import bj4.dev.yhh.repository.entity.LtoEntity
import bj4.dev.yhh.repository.entity.LtoHKEntity
import com.airbnb.epoxy.Typed2EpoxyController
import com.airbnb.epoxy.Typed3EpoxyController
import java.lang.IllegalArgumentException

class SmallTableViewController : Typed3EpoxyController<List<LotteryEntity>, Int, Int>() {
    override fun buildModels(list: List<LotteryEntity>, lotteryType: Int, diffValue: Int) {
        if (list.isEmpty()) return

        when (lotteryType) {
            LotteryType.LtoBig -> {
                for (index in list.indices) {
                    val item = list[index] as LtoBigEntity

                    smallTableLtoBigViewHolder {
                        id("$lotteryType$item")
                        entity(item)
                        diffValue(diffValue)
                        if (index > 0) {
                            diffEntity(list[index - 1])
                        }
                    }
                }
            }
            LotteryType.Lto -> {
                for (index in list.indices) {
                    val item = list[index] as LtoEntity

                    smallTableLtoViewHolder {
                        id("$lotteryType$item")
                        entity(item)
                        diffValue(diffValue)
                        if (index > 0) {
                            diffEntity(list[index - 1])
                        }
                    }
                }
            }
            LotteryType.LtoHK -> {
                for (index in list.indices) {
                    val item = list[index] as LtoHKEntity

                    smallTableLtoHKViewHolder {
                        id("$lotteryType$item")
                        entity(item)
                        diffValue(diffValue)
                        if (index > 0) {
                            diffEntity(list[index - 1])
                        }
                    }
                }
            }
            else -> throw IllegalArgumentException("Wrong type")
        }
    }
}