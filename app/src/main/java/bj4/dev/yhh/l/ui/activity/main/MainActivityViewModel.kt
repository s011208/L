package bj4.dev.yhh.l.ui.activity.main

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bj4.dev.yhh.l.util.SharedPreferenceHelper
import timber.log.Timber

class MainActivityViewModel(private val sharedPreferenceHelper: SharedPreferenceHelper) :
    ViewModel(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val _sortingType = MutableLiveData<Int>().apply {
        value = sharedPreferenceHelper.getSortingType()
    }

    val sortingType: LiveData<Int> = _sortingType

    fun resume() {
        sharedPreferenceHelper.generalSettings.registerOnSharedPreferenceChangeListener(this)
    }

    fun pause() {
        sharedPreferenceHelper.generalSettings.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        Timber.v("key: $key changed")
        if (key == SharedPreferenceHelper.KEY_DISPLAY_TYPE) {
            _sortingType.value = sharedPreferenceHelper.getSortingType()
        }
    }
}