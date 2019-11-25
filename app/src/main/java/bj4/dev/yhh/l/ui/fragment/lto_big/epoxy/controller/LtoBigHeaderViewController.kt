package bj4.dev.yhh.l.ui.fragment.lto_big.epoxy.controller

import bj4.dev.yhh.l.ui.fragment.lto_big.epoxy.viewholder.ltoBigViewHolder
import bj4.dev.yhh.repository.entity.LtoBigEntity
import com.airbnb.epoxy.TypedEpoxyController

class LtoBigHeaderViewController :
    TypedEpoxyController<Int>() {
    override fun buildModels(sortingType: Int) {
        ltoBigViewHolder {
            id("$sortingType-header")
            entity(LtoBigEntity(0, ArrayList(), ArrayList(), ArrayList()))
            header(true)
            sortingType(sortingType)
        }
    }
}