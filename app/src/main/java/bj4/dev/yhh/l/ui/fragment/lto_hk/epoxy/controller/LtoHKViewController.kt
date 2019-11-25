package bj4.dev.yhh.l.ui.fragment.lto_hk.epoxy.controller

import bj4.dev.yhh.l.ui.fragment.lto_hk.epoxy.viewholder.ltoHKViewHolder
import bj4.dev.yhh.repository.entity.LtoHKEntity
import com.airbnb.epoxy.Typed2EpoxyController

class LtoHKViewController :
    Typed2EpoxyController<List<LtoHKEntity>, Int>() {
    override fun buildModels(data: List<LtoHKEntity>, sortingType: Int) {
        data.forEach { item ->
            ltoHKViewHolder {
                id("$sortingType$item")
                entity(item)
                sortingType(sortingType)
            }
        }
    }
}