package bj4.dev.yhh.l.ui.fragment.lto_big.epoxy.controller

import bj4.dev.yhh.l.ui.fragment.lto_big.epoxy.viewholder.ltoBigViewHolder
import bj4.dev.yhh.repository.entity.LtoBigEntity
import com.airbnb.epoxy.Typed2EpoxyController

class LtoBigViewController :
    Typed2EpoxyController<List<LtoBigEntity>, Int>() {
    override fun buildModels(data: List<LtoBigEntity>, sortingType: Int) {
        data.forEach { item ->
            ltoBigViewHolder {
                id("$sortingType$item")
                entity(item)
                sortingType(sortingType)
            }
        }
    }
}