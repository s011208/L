package bj4.dev.yhh.l.ui.activity.log

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import bj4.dev.yhh.l.R
import kotlinx.android.synthetic.main.activity_log.*
import org.koin.android.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class LogActivity : AppCompatActivity() {

    companion object {
        const val TYPE_JOB_SERVICE_TIME = 1
        const val TYPE_UPDATE_LOTTERY_TIME = 2

        const val EXTRA_TYPE = "type"
    }

    lateinit var activityViewModel: LogActivityViewModel

    private val adapter = LogActivityAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        activityViewModel = getViewModel(
            LogActivityViewModel::class,
            qualifier = null
        ) { parametersOf(intent.getIntExtra(EXTRA_TYPE, TYPE_JOB_SERVICE_TIME)) }

        activityViewModel.listData.observe(this, Observer {
            adapter.items.clear()
            adapter.items.addAll(it)
            adapter.notifyDataSetChanged()
        })

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)

        activityViewModel.load()
    }
}