package bj4.dev.yhh.l.ui.fragment.lto_hk.epoxy.controller

import bj4.dev.yhh.l.ui.fragment.lto_hk.epoxy.viewholder.ltoHKViewHolder
import bj4.dev.yhh.repository.entity.LtoHKEntity
import com.airbnb.epoxy.TypedEpoxyController

class LtoHKHeaderViewController :
    TypedEpoxyController<Int>() {
    override fun buildModels(sortingType: Int) {
        ltoHKViewHolder {
            id("$sortingType-header")
            entity(LtoHKEntity(0, ArrayList(), ArrayList(), ArrayList()))
            header(true)
            sortingType(sortingType)
        }
    }
}