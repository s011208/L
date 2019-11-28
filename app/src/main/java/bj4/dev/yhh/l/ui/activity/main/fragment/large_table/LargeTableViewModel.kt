package bj4.dev.yhh.l.ui.activity.main.fragment.large_table

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bj4.dev.yhh.l.util.SharedPreferenceHelper
import bj4.dev.yhh.repository.LotteryType
import bj4.dev.yhh.repository.entity.LotteryEntity
import bj4.dev.yhh.repository.repository.LotteryRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class LargeTableViewModel(
    private val lotteryRepository: LotteryRepository,
    private val sharedPreferenceHelper: SharedPreferenceHelper
) : ViewModel() {

    private val _rawData = MutableLiveData<Pair<List<LotteryEntity>, Int>>().apply {
        value = Pair(ArrayList(), sharedPreferenceHelper.getSortingType())
    }

    val rawData: LiveData<Pair<List<LotteryEntity>, Int>> = _rawData

    private val compositeDisposable = CompositeDisposable()

    private var ltoType: Int = 0

    fun load() {
        when (ltoType) {
            LotteryType.LtoHK -> {
                compositeDisposable += lotteryRepository.getLtoHK()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { return@map it.subList(0, it.size) }
                    .subscribe(
                        { list ->
                            Timber.i("LtoHK item size: ${list.size}")
                            _rawData.value = Pair(list, sharedPreferenceHelper.getSortingType())
                        },
                        {
                            Timber.w(it, "failed")
                        }
                    )
            }
            LotteryType.LtoBig -> {
                compositeDisposable += lotteryRepository.getLtoBig()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { list ->
                            Timber.i("LtoBig item size: ${list.size}")
                            _rawData.value = Pair(list, sharedPreferenceHelper.getSortingType())
                        },
                        {
                            Timber.w(it, "failed")
                        }
                    )
            }
            LotteryType.Lto -> {
                compositeDisposable += lotteryRepository.getLto()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { list ->
                            Timber.i("Lto item size: ${list.size}")
                            _rawData.value = Pair(list, sharedPreferenceHelper.getSortingType())
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