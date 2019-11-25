package bj4.dev.yhh.l.ui.fragment.lto.epoxy.controller

import bj4.dev.yhh.l.ui.fragment.lto.epoxy.viewholder.ltoViewHolder
import bj4.dev.yhh.repository.entity.LtoEntity
import com.airbnb.epoxy.Typed2EpoxyController

class LtoViewController :
    Typed2EpoxyController<List<LtoEntity>, Int>() {
    override fun buildModels(data: List<LtoEntity>, sortingType: Int) {
        data.forEach { item ->
            ltoViewHolder {
                id("$sortingType$item")
                entity(item)
                sortingType(sortingType)
            }
        }
    }
}