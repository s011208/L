package bj4.dev.yhh.l.ui.fragment.large_table.epoxy.controller

import bj4.dev.yhh.l.ui.fragment.large_table.lto.epoxy.viewholder.ltoViewHolder
import bj4.dev.yhh.l.ui.fragment.large_table.lto_big.epoxy.viewholder.ltoBigViewHolder
import bj4.dev.yhh.l.ui.fragment.large_table.lto_hk.epoxy.viewholder.ltoHKViewHolder
import bj4.dev.yhh.repository.LotteryType
import bj4.dev.yhh.repository.entity.LtoBigEntity
import bj4.dev.yhh.repository.entity.LtoEntity
import bj4.dev.yhh.repository.entity.LtoHKEntity
import com.airbnb.epoxy.Typed2EpoxyController

class LargeTableHeaderViewController :
    Typed2EpoxyController<Int, Int>() {
    override fun buildModels(sortingType: Int, ltoType: Int) {
        when (ltoType) {
            LotteryType.LtoHK -> {
                ltoHKViewHolder {
                    id("$sortingType-header")
                    entity(LtoHKEntity(0, ArrayList(), ArrayList(), ArrayList()))
                    header(true)
                    sortingType(sortingType)
                }
            }
            LotteryType.LtoBig -> {
                ltoBigViewHolder {
                    id("$sortingType-header")
                    entity(LtoBigEntity(0, ArrayList(), ArrayList(), ArrayList()))
                    header(true)
                    sortingType(sortingType)
                }
            }
            LotteryType.Lto -> {
                ltoViewHolder {
                    id("$sortingType-header")
                    entity(LtoEntity(0, ArrayList(), ArrayList(), ArrayList(), ArrayList()))
                    header(true)
                    sortingType(sortingType)
                }
            }
        }
    }
}
