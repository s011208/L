package bj4.dev.yhh.l.ui.fragment.lto_big

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bj4.dev.yhh.l.util.SharedPreferenceHelper
import bj4.dev.yhh.repository.entity.LtoBigEntity
import bj4.dev.yhh.repository.repository.LotteryRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class LtoBigViewModel(
    private val lotteryRepository: LotteryRepository,
    private val sharedPreferenceHelper: SharedPreferenceHelper
) : ViewModel(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val _rawData = MutableLiveData<List<LtoBigEntity>>().apply {
        value = ArrayList()
    }

    val rawData: LiveData<List<LtoBigEntity>> = _rawData

    private val _sortingType = MutableLiveData<Int>().apply {
        value = sharedPreferenceHelper.getDisplayType()
    }

    val sortingType: LiveData<Int> = _sortingType

    private val compositeDisposable = CompositeDisposable()

    fun resume() {
        sharedPreferenceHelper.generalSettings.registerOnSharedPreferenceChangeListener(this)
    }

    fun pause() {
        sharedPreferenceHelper.generalSettings.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        Timber.v("key: $key changed")
        if (key == SharedPreferenceHelper.KEY_DISPLAY_TYPE) {
            _sortingType.value = sharedPreferenceHelper.getDisplayType()
        }
    }

    fun load() {
        compositeDisposable += lotteryRepository.getLtoBig()
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

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}