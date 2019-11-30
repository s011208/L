package bj4.dev.yhh.l.ui.activity.log

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bj4.dev.yhh.log.LogHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class LogActivityViewModel(private val logType: Int, private val logHelper: LogHelper) :
    ViewModel() {

    init {
        Timber.v("logType: $logType")
    }

    private val _listData = MutableLiveData<List<LogData>>().apply {
        value = ArrayList()
    }

    val listData: LiveData<List<LogData>> = _listData

    private val compositeDisposable = CompositeDisposable()

    fun load() {
        when (logType) {
            LogActivity.TYPE_JOB_SERVICE_TIME -> {
                compositeDisposable += logHelper.queryJobServiceEntityList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { list ->
                        return@map ArrayList<LogData>().apply {
                            list.reversed().forEach { entity ->
                                add(LogData(entity.timeStamp, entity.message))
                            }
                        }
                    }.subscribe(
                        {
                            _listData.value = it
                        },
                        {
                            Timber.w("Failed to get data")
                        }
                    )
            }
            LogActivity.TYPE_UPDATE_LOTTERY_TIME -> {
                compositeDisposable += logHelper.queryUpdateServiceTimeEntityList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { list ->
                        return@map ArrayList<LogData>().apply {
                            list.reversed().forEach { entity ->
                                add(LogData(entity.timeStamp, entity.message))
                            }
                        }
                    }.subscribe(
                        {
                            _listData.value = it
                        },
                        {
                            Timber.w("Failed to get data")
                        }
                    )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}