package bj4.dev.yhh.l.ui.fragment.lto.epoxy.controller

import bj4.dev.yhh.l.ui.fragment.lto.epoxy.viewholder.ltoViewHolder
import bj4.dev.yhh.repository.entity.LtoEntity
import com.airbnb.epoxy.TypedEpoxyController

class LtoHeaderViewController :
    TypedEpoxyController<Int>() {
    override fun buildModels(sortingType: Int) {
        ltoViewHolder {
            id("$sortingType-header")
            entity(LtoEntity(0, ArrayList(), ArrayList(), ArrayList(), ArrayList()))
            header(true)
            sortingType(sortingType)
        }
    }
}