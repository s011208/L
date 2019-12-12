package bj4.dev.yhh.l.ui.activity.main.fragment.list_table

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import bj4.dev.yhh.job_schedulers.UpdateLotteryIntentService
import bj4.dev.yhh.l.BuildConfig
import bj4.dev.yhh.l.R
import bj4.dev.yhh.l.ui.TrackableFragment
import bj4.dev.yhh.l.ui.activity.main.MainActivityActions
import bj4.dev.yhh.l.util.SharedPreferenceHelper
import bj4.dev.yhh.repository.entity.LotteryEntity
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.fragment_small_table.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

abstract class ListTableFragment : TrackableFragment(), MainActivityActions {
    companion object {
        private const val ARGUMENT_LTO_TYPE = "lto_type"
    }

    private var ltoType: Int = 0

    private val fragmentViewModel: ListTableViewModel by viewModel()
    private val sharedPreferenceHelper: SharedPreferenceHelper by inject()

    private lateinit var adapter: ListTableRecyclerViewAdapter

    private var list: List<LotteryEntity> = ArrayList()

    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_table, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentViewModel.setLtoType((arguments?.getInt(ARGUMENT_LTO_TYPE) ?: 0)
            .also {
                ltoType = it
                adapter = ListTableRecyclerViewAdapter(it)
            }
        )

        fragmentViewModel.rawData.observe(this, Observer {
            list = it
            adapter.itemList.clear()
            adapter.itemList.addAll(list)
            adapter.notifyDataSetChanged()
        })

        fragmentViewModel.isLoading.observe(this, Observer {
            progressBar.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })

        fragmentViewModel.showInitHint.observe(this, Observer {
            initHint.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })

        fragmentViewModel.updateLotteryService.observe(this, Observer { action ->
            requireContext().startService(
                Intent(
                    requireContext(),
                    UpdateLotteryIntentService::class.java
                ).apply {
                    this.action = action
                })
        })

        compositeDisposable += initHint.clicks().subscribe {
            fragmentViewModel.requestUpdate()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        updateTextSize()

        fragmentViewModel.load()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.v("onActivityResult, requestCode: $requestCode, resultCode: $resultCode")
        updateTextSize(true)
    }

    private fun updateTextSize(update: Boolean = false) {
        sharedPreferenceHelper.getListTableTextSize().also { textSize ->
            adapter.textSize = textSize
            Timber.v("textSize: $textSize")
        }

        sharedPreferenceHelper.getListTableCellWidth().also { cellWidth ->
            adapter.cellWidth = cellWidth
            Timber.v("cellWidth: $cellWidth")
        }

        sharedPreferenceHelper.getListTableCellDateWidth().also { cellDateWidth ->
            adapter.cellDateWidth = cellDateWidth
            Timber.v("cellDateWidth: $cellDateWidth")
        }

        if (update) {
            adapter.notifyDataSetChanged()
        }
    }

    override fun onSortingTypeChanged(sortingType: Int) {
        if (BuildConfig.DEBUG) throw IllegalStateException("Cannot have sorting call back")
    }

    override fun onMoveToBottom() {
        recyclerView.scrollToPosition(recyclerView.adapter?.itemCount?.minus(1) ?: 0)
    }

    override fun onMoveToTop() {
        recyclerView.scrollToPosition(0)
    }
}