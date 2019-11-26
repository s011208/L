package bj4.dev.yhh.l.ui.fragment.small_table

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

    val rawData: LiveData<List<LotteryEntity>> = _rawData

    private var ltoType: Int = 0

    private val compositeDisposable = CompositeDisposable()

    fun load() {
        when (ltoType) {
            LotteryType.LtoHK -> {
                compositeDisposable += lotteryRepository.getLtoHK()
                    .map { list -> return@map list.filter { !it.isSubTotal } }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { list ->
                            Timber.i("LtoHK item size: ${list.size}")
                            _rawData.value = list
                        },
                        {
                            Timber.w(it, "failed")
                        }
                    )
            }
            LotteryType.LtoBig -> {
                compositeDisposable += lotteryRepository.getLtoBig()
                    .map { list -> return@map list.filter { !it.isSubTotal } }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { list ->
                            Timber.i("LtoBig item size: ${list.size}")
                            _rawData.value = list
                        },
                        {
                            Timber.w(it, "failed")
                        }
                    )
            }
            LotteryType.Lto -> {
                compositeDisposable += lotteryRepository.getLto()
                    .map { list -> return@map list.filter { !it.isSubTotal } }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { list ->
                            Timber.i("Lto item size: ${list.size}")
                            _rawData.value = list
                        },
                        {
                            Timber.w(it, "failed")
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