package bj4.dev.yhh.l.ui.fragment.small_table

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import bj4.dev.yhh.l.R
import bj4.dev.yhh.l.ui.activity.main.MainActivityActions
import bj4.dev.yhh.repository.entity.LotteryEntity
import kotlinx.android.synthetic.main.fragment_large_table.recyclerView
import kotlinx.android.synthetic.main.fragment_small_table.*
import org.koin.android.viewmodel.ext.android.viewModel

abstract class SmallTableFragment : Fragment(), MainActivityActions,
    CompoundButton.OnCheckedChangeListener {

    companion object {
        private const val ARGUMENT_LTO_TYPE = "lto_type"
    }

    val fragmentViewModel: SmallTableViewModel by viewModel()

    private lateinit var adapter: SmallTableRecyclerViewAdapter

    private var ltoType: Int = 0

    private var diffValue: Int = 0

    private var diffSign = 1

    private var diffNumber = 0

    private var list: List<LotteryEntity> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_small_table, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentViewModel.setLtoType((arguments?.getInt(ARGUMENT_LTO_TYPE) ?: 0)
            .also {
                ltoType = it
                adapter = SmallTableRecyclerViewAdapter(it)
            }
        )

        fragmentViewModel.rawData.observe(this, Observer {
            list = it
            adapter.itemList.clear()
            adapter.itemList.addAll(list)
            adapter.diffValue = diffValue
            adapter.notifyDataSetChanged()
        })

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        radioButton0.setOnCheckedChangeListener(this)
        radioButton1.setOnCheckedChangeListener(this)
        radioButton2.setOnCheckedChangeListener(this)
        radioButton3.setOnCheckedChangeListener(this)
        radioButton4.setOnCheckedChangeListener(this)
        radioButton5.setOnCheckedChangeListener(this)
        radioButton6.setOnCheckedChangeListener(this)
        radioButton7.setOnCheckedChangeListener(this)
        radioButton8.setOnCheckedChangeListener(this)
        radioButton9.setOnCheckedChangeListener(this)
        radioButton10.setOnCheckedChangeListener(this)
        radioButton11.setOnCheckedChangeListener(this)
        radioButton12.setOnCheckedChangeListener(this)
        radioButton13.setOnCheckedChangeListener(this)
        radioButton14.setOnCheckedChangeListener(this)
        radioButton15.setOnCheckedChangeListener(this)
        radioButton16.setOnCheckedChangeListener(this)
        radioButton17.setOnCheckedChangeListener(this)
        radioButton18.setOnCheckedChangeListener(this)
        radioButton19.setOnCheckedChangeListener(this)
        radioButton20.setOnCheckedChangeListener(this)

        radioButtonPlus.setOnCheckedChangeListener(this)
        radioButtonMinus.setOnCheckedChangeListener(this)

        fragmentViewModel.load()
    }

    override fun onSortingTypeChanged(sortingType: Int) {
    }

    override fun onMoveToBottom() {
        recyclerView.scrollToPosition(recyclerView.adapter?.itemCount?.minus(1) ?: 0)
    }

    override fun onMoveToTop() {
        recyclerView.scrollToPosition(0)
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (!isChecked) return
        when (buttonView.id) {
            R.id.radioButton0 -> diffNumber = 0
            R.id.radioButton1 -> diffNumber = 1
            R.id.radioButton2 -> diffNumber = 2
            R.id.radioButton3 -> diffNumber = 3
            R.id.radioButton4 -> diffNumber = 4
            R.id.radioButton5 -> diffNumber = 5
            R.id.radioButton6 -> diffNumber = 6
            R.id.radioButton7 -> diffNumber = 7
            R.id.radioButton8 -> diffNumber = 8
            R.id.radioButton9 -> diffNumber = 9
            R.id.radioButton10 -> diffNumber = 10
            R.id.radioButton11 -> diffNumber = 11
            R.id.radioButton12 -> diffNumber = 12
            R.id.radioButton13 -> diffNumber = 13
            R.id.radioButton14 -> diffNumber = 14
            R.id.radioButton15 -> diffNumber = 15
            R.id.radioButton16 -> diffNumber = 16
            R.id.radioButton17 -> diffNumber = 17
            R.id.radioButton18 -> diffNumber = 18
            R.id.radioButton19 -> diffNumber = 19
            R.id.radioButton20 -> diffNumber = 20
            R.id.radioButtonPlus -> diffSign = 1
            R.id.radioButtonMinus -> diffSign = -1
        }
        diffValue = diffSign * diffNumber

        adapter.diffValue = diffValue
        adapter.notifyDataSetChanged()
    }
}