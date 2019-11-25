package bj4.dev.yhh.l.ui.fragment.lto_hk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import bj4.dev.yhh.l.R
import bj4.dev.yhh.l.ui.fragment.lto_hk.epoxy.controller.LtoHKHeaderViewController
import bj4.dev.yhh.l.ui.fragment.lto_hk.epoxy.controller.LtoHKViewController
import bj4.dev.yhh.l.util.SharedPreferenceHelper
import bj4.dev.yhh.repository.entity.LtoHKEntity
import kotlinx.android.synthetic.main.fragment_lto_hk.*
import org.koin.android.viewmodel.ext.android.viewModel


class LtoHKFragment : Fragment() {

    val fragmentViewModel: LtoHKViewModel by viewModel()

    private val recyclerViewController = LtoHKViewController()
    private val headerController = LtoHKHeaderViewController()
    private val footerController = LtoHKViewController()

    private var data: List<LtoHKEntity> = ArrayList()
    private var sortingType = SharedPreferenceHelper.DISPLAY_TYPE_ORDER

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lto_hk, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = recyclerViewController.adapter

        fragmentViewModel.rawData.observe(this, Observer {
            data = it
            recyclerViewController.setData(data, sortingType)
        })

        fragmentViewModel.sortingType.observe(this, Observer {
            sortingType = it
            recyclerViewController.setData(data, sortingType)
            headerController.setData(sortingType)
        })

        header.layoutManager = LinearLayoutManager(requireContext())
        header.adapter = headerController.adapter

        footer.layoutManager = LinearLayoutManager(requireContext())
        footer.adapter = footerController.adapter

        fragmentViewModel.load()
    }

    override fun onResume() {
        super.onResume()
        fragmentViewModel.resume()
    }

    override fun onPause() {
        super.onPause()
        fragmentViewModel.pause()
    }
}