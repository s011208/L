package bj4.dev.yhh.l.ui.fragment.large_table

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import bj4.dev.yhh.l.R
import bj4.dev.yhh.l.ui.activity.main.MainActivityActions
import bj4.dev.yhh.l.ui.fragment.large_table.epoxy.controller.LargeTableHeaderViewController
import bj4.dev.yhh.l.ui.fragment.large_table.epoxy.controller.LargeTableViewController
import bj4.dev.yhh.l.util.SharedPreferenceHelper
import bj4.dev.yhh.repository.entity.LotteryEntity
import kotlinx.android.synthetic.main.fragment_lto_hk.*
import org.koin.android.viewmodel.ext.android.viewModel

abstract class LargeTableFragment : Fragment(), MainActivityActions {

    companion object {
        private const val ARGUMENT_LTO_TYPE = "lto_type"
    }

    val fragmentViewModel: LargeTableViewModel by viewModel()

    private val recyclerViewController = LargeTableViewController()
    private val headerController = LargeTableHeaderViewController()
    private val footerController = LargeTableViewController()

    private var data: List<LotteryEntity> = ArrayList()
    private var sortingType = SharedPreferenceHelper.DISPLAY_TYPE_ORDER
    private var ltoType: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_large_table, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentViewModel.setLtoType((arguments?.getInt(ARGUMENT_LTO_TYPE) ?: 0)
            .also {
                ltoType = it
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = recyclerViewController.adapter

        fragmentViewModel.rawData.observe(this, Observer {
            data = it.first
            sortingType = it.second
            recyclerViewController.setData(data, sortingType, ltoType)
            headerController.setData(sortingType, ltoType)
        })

        header.layoutManager = LinearLayoutManager(requireContext())
        header.adapter = headerController.adapter

        footer.layoutManager = LinearLayoutManager(requireContext())
        footer.adapter = footerController.adapter

        fragmentViewModel.load()
    }

    override fun onSortingTypeChanged(sortingType: Int) {
        this.sortingType = sortingType
        recyclerViewController.setData(data, sortingType, ltoType)
        headerController.setData(sortingType, ltoType)
    }

    override fun onMoveToBottom() {
        recyclerView.scrollToPosition(recyclerView.adapter?.itemCount?.minus(1) ?: 0)
    }

    override fun onMoveToTop() {
        recyclerView.scrollToPosition(0)
    }
}