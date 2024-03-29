package bj4.dev.yhh.l.ui.activity.main.fragment.large_table

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import bj4.dev.yhh.job_schedulers.UpdateLotteryIntentService
import bj4.dev.yhh.l.R
import bj4.dev.yhh.l.ui.TrackableFragment
import bj4.dev.yhh.l.ui.activity.main.MainActivityActions
import bj4.dev.yhh.l.util.SharedPreferenceHelper
import bj4.dev.yhh.repository.entity.LotteryEntity
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.fragment_large_table.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

abstract class LargeTableFragment : TrackableFragment(), MainActivityActions {

    companion object {
        private const val ARGUMENT_LTO_TYPE = "lto_type"
    }

    private val fragmentViewModel: LargeTableViewModel by viewModel()
    private val sharedPreferenceHelper: SharedPreferenceHelper by inject()

    private lateinit var recyclerViewAdapter: LargeTableRecyclerViewAdapter
    private lateinit var headerAdapter: LargeTableRecyclerViewAdapter

    private val compositeDisposable = CompositeDisposable()

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
                recyclerViewAdapter = LargeTableRecyclerViewAdapter(it, false)
                headerAdapter = LargeTableRecyclerViewAdapter(it, true)
            }
        )

        updateTextSize()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = recyclerViewAdapter
        compositeDisposable += recyclerViewAdapter.clickIntent.subscribe {
            recyclerViewAdapter.notifyItemChanged(recyclerViewAdapter.selectedIndex)
            recyclerViewAdapter.selectedIndex = it
            recyclerViewAdapter.notifyItemChanged(it)
        }

        fragmentViewModel.rawData.observe(this, Observer {
            data = it.first
            sortingType = it.second
            recyclerViewAdapter.sortingType = sortingType
            recyclerViewAdapter.itemList.clear()
            recyclerViewAdapter.itemList.addAll(data)
            recyclerViewAdapter.notifyDataSetChanged()
            headerAdapter.sortingType = sortingType
            headerAdapter.notifyDataSetChanged()
        })

        fragmentViewModel.isLoading.observe(this, Observer {
            progressBar.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })

        fragmentViewModel.showScrollView.observe(this, Observer {
            scrollView.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })

        fragmentViewModel.showInitHint.observe(this, Observer {
            initHint.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })

        header.layoutManager = LinearLayoutManager(requireContext())
        header.adapter = headerAdapter

        compositeDisposable += initHint.clicks().subscribe {
            fragmentViewModel.requestUpdate()
        }

        fragmentViewModel.updateLotteryService.observe(this, Observer { action ->
            requireContext().startService(
                Intent(
                    requireContext(),
                    UpdateLotteryIntentService::class.java
                ).apply {
                    this.action = action
                })
        })

        fragmentViewModel.load()
    }

    override fun onSortingTypeChanged(sortingType: Int) {
        Timber.v("onSortingTypeChanged start")
        this.sortingType = sortingType
        recyclerViewAdapter.sortingType = sortingType
        recyclerViewAdapter.itemList.clear()
        recyclerViewAdapter.itemList.addAll(data)
        recyclerViewAdapter.notifyDataSetChanged()
        headerAdapter.sortingType = sortingType
        headerAdapter.notifyDataSetChanged()
        Timber.v("onSortingTypeChanged end")
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    override fun onMoveToBottom() {
        recyclerView.scrollToPosition(recyclerView.adapter?.itemCount?.minus(1) ?: 0)
    }

    override fun onMoveToTop() {
        recyclerView.scrollToPosition(0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.v("onActivityResult, requestCode: $requestCode, resultCode: $resultCode")
        updateTextSize(true)
    }

    private fun updateTextSize(update: Boolean = false) {
        sharedPreferenceHelper.getLargeTableTextSize().also { textSize ->
            recyclerViewAdapter.textSize = textSize
            headerAdapter.textSize = textSize
        }

        sharedPreferenceHelper.getLargeTableCellWidth().also { cellWidth ->
            recyclerViewAdapter.cellWidth = cellWidth
            headerAdapter.cellWidth = cellWidth
        }

        sharedPreferenceHelper.getLargeTableCellDateWidth().also { cellDateWidth ->
            recyclerViewAdapter.cellDateWidth = cellDateWidth
            headerAdapter.cellDateWidth = cellDateWidth
        }

        if (update) {
            recyclerViewAdapter.notifyDataSetChanged()
            headerAdapter.notifyDataSetChanged()
        }
    }
}