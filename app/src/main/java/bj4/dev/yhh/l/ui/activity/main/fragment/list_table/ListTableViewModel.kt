package bj4.dev.yhh.l.ui.activity.main.fragment.list_table

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bj4.dev.yhh.job_schedulers.UpdateLotteryIntentService
import bj4.dev.yhh.repository.LotteryType
import bj4.dev.yhh.repository.entity.LotteryEntity
import bj4.dev.yhh.repository.repository.LotteryRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class ListTableViewModel(private val lotteryRepository: LotteryRepository) : ViewModel() {

    private val _rawData = MutableLiveData<List<LotteryEntity>>().apply {
        value = ArrayList()
    }

    private val _isLoading = MutableLiveData<Boolean>().apply { value = false }

    private val _showInitHint = MutableLiveData<Boolean>().apply { value = false }

    private val _updateLotteryService = MutableLiveData<String>()

    val rawData: LiveData<List<LotteryEntity>> = _rawData

    val isLoading: LiveData<Boolean> = _isLoading

    val showInitHint: LiveData<Boolean> = _showInitHint

    val updateLotteryService: LiveData<String> = _updateLotteryService

    private var ltoType: Int = 0

    private val compositeDisposable = CompositeDisposable()

    fun requestUpdate() {
        when (ltoType) {
            LotteryType.LtoList3 -> {
                _updateLotteryService.value = UpdateLotteryIntentService.ACTION_UPDATE_LTO_LIST3
            }
            LotteryType.LtoList4 -> {
                _updateLotteryService.value = UpdateLotteryIntentService.ACTION_UPDATE_LTO_LIST4
            }
            else -> {
                throw IllegalArgumentException("wrong lto type: $ltoType")
            }
        }
    }

    fun load() {
        when (ltoType) {
            LotteryType.LtoList3 -> {
                compositeDisposable += lotteryRepository.getLtoList3LiveData()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe {
                        _isLoading.value = true
                        _showInitHint.value = false
                    }
                    .subscribe(
                        { list ->
                            Timber.i("LtoList3 item size: ${list.size}")
                            _isLoading.value = false
                            _rawData.value = list
                            _showInitHint.value = list.isEmpty()
                        },
                        {
                            Timber.w(it, "failed")
                        },
                        {
                            _isLoading.value = false
                        }
                    )
            }
            LotteryType.LtoList4 -> {
                compositeDisposable += lotteryRepository.getLtoList4LiveData()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe {
                        _isLoading.value = true
                        _showInitHint.value = false
                    }
                    .subscribe(
                        { list ->
                            Timber.i("LtoList4 item size: ${list.size}")
                            _isLoading.value = false
                            _rawData.value = list
                            _showInitHint.value = list.isEmpty()
                        },
                        {
                            Timber.w(it, "failed")
                        },
                        {
                            _isLoading.value = false
                        }
                    )
            }
            else -> {
                throw IllegalArgumentException("wrong lto type: $ltoType")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun setLtoType(@LotteryType ltoType: Int) {
        this.ltoType = ltoType
    }
}