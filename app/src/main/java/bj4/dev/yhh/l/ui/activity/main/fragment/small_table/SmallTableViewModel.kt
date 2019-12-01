package bj4.dev.yhh.l.ui.activity.main.fragment.small_table

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bj4.dev.yhh.repository.LotteryType
import bj4.dev.yhh.repository.entity.LotteryEntity
import bj4.dev.yhh.repository.repository.LotteryRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class SmallTableViewModel(private val lotteryRepository: LotteryRepository) : ViewModel() {

    private val _rawData = MutableLiveData<List<LotteryEntity>>().apply {
        value = ArrayList()
    }

    private val _isLoading = MutableLiveData<Boolean>().apply { value = false }

    val rawData: LiveData<List<LotteryEntity>> = _rawData

    val isLoading: LiveData<Boolean> = _isLoading

    private var ltoType: Int = 0

    private val compositeDisposable = CompositeDisposable()

    fun load() {
        when (ltoType) {
            LotteryType.LtoHK -> {
                compositeDisposable += lotteryRepository.getLtoHK()
                    .map { list -> return@map list.filter { !it.isSubTotal } }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { _isLoading.value = true }
                    .subscribe(
                        { list ->
                            Timber.i("LtoHK item size: ${list.size}")
                            _rawData.value = list
                        },
                        {
                            Timber.w(it, "failed")
                        },
                        {
                            _isLoading.value = false
                        }
                    )
            }
            LotteryType.LtoBig -> {
                compositeDisposable += lotteryRepository.getLtoBig()
                    .map { list -> return@map list.filter { !it.isSubTotal } }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { _isLoading.value = true }
                    .subscribe(
                        { list ->
                            Timber.i("LtoBig item size: ${list.size}")
                            _rawData.value = list
                        },
                        {
                            Timber.w(it, "failed")
                        },
                        {
                            _isLoading.value = false
                        }
                    )
            }
            LotteryType.Lto -> {
                compositeDisposable += lotteryRepository.getLto()
                    .map { list -> return@map list.filter { !it.isSubTotal } }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { _isLoading.value = true }
                    .subscribe(
                        { list ->
                            Timber.i("Lto item size: ${list.size}")
                            _rawData.value = list
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