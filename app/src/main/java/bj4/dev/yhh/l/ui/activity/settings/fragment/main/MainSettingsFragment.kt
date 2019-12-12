package bj4.dev.yhh.l.ui.activity.settings.fragment.main

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import bj4.dev.yhh.job_schedulers.UpdateLotteryIntentService
import bj4.dev.yhh.l.R
import bj4.dev.yhh.l.ui.activity.log.LogActivity
import bj4.dev.yhh.l.ui.activity.settings.SettingsActivity
import bj4.dev.yhh.repository.repository.LotteryRepository
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject
import timber.log.Timber

class MainSettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        private const val KEY_JOB_SERVICE_LOG = "key_job_service_log"
        private const val KEY_UPDATE_TIME_SERVICE_LOG = "key_update_time_service_log"

        private const val KEY_RESET_ALL_DATA = "key_reset_all_data"
        private const val KEY_UPDATE_ALL_DATA = "key_update_all_data"
        private const val KET_CLEAR_ALL_DATA = "key_clear_all_data"

        const val KEY_LARGE_TABLE_TEXT_SIZE = "key_large_table_text_size"
        const val KEY_SMALL_TABLE_TEXT_SIZE = "key_small_table_text_size"
        const val KEY_LIST_TABLE_TEXT_SIZE = "key_list_table_text_size"

        const val KEY_ALWAYS_USE_HORIZONTAL = "key_always_use_horizontal"
    }

    val repository: LotteryRepository by inject()

    private val compositeDisposable = CompositeDisposable()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_main)

        updateLargeTableTextSizeSummary()
        updateSmallTableTextSizeSummary()
        updateListTableTextSizeSummary()
    }

    private fun updateLargeTableTextSizeSummary() {
        findPreference<ListPreference>(KEY_LARGE_TABLE_TEXT_SIZE)?.apply {
            summary = entry
        }
    }

    private fun updateSmallTableTextSizeSummary() {
        findPreference<ListPreference>(KEY_SMALL_TABLE_TEXT_SIZE)?.apply {
            summary = entry
        }
    }

    private fun updateListTableTextSizeSummary() {
        findPreference<ListPreference>(KEY_LIST_TABLE_TEXT_SIZE)?.apply {
            summary = entry
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        return when (preference.key) {
            KEY_JOB_SERVICE_LOG -> {
                startActivity(Intent(requireContext(), LogActivity::class.java).apply {
                    putExtra(
                        LogActivity.EXTRA_TYPE,
                        LogActivity.TYPE_JOB_SERVICE_TIME
                    )
                })
                true
            }
            KEY_UPDATE_TIME_SERVICE_LOG -> {
                startActivity(Intent(requireContext(), LogActivity::class.java).apply {
                    putExtra(
                        LogActivity.EXTRA_TYPE,
                        LogActivity.TYPE_UPDATE_LOTTERY_TIME
                    )
                })

                true
            }
            KEY_RESET_ALL_DATA -> {
                compositeDisposable += Completable.fromCallable {
                    stopUpdateLotteryService()
                    nukeAllTables()
                }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete {
                        updateAllData()
                    }
                    .subscribe(
                        {
                            Toast.makeText(
                                requireContext(),
                                R.string.settings_clear_all_data_toast,
                                Toast.LENGTH_LONG
                            ).show()
                        },
                        {
                            Timber.w(it, "failed to nuke table")
                        }
                    )
                true
            }
            KET_CLEAR_ALL_DATA -> {
                Completable.fromCallable {
                    nukeAllTables()
                }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            Toast.makeText(
                                requireContext(),
                                R.string.settings_clear_all_data_toast,
                                Toast.LENGTH_LONG
                            ).show()
                        },
                        {
                            Timber.w(it, "failed to nuke table")
                        }
                    )
                true
            }
            KEY_UPDATE_ALL_DATA -> {
                stopUpdateLotteryService()
                updateAllData()
                true
            }
            else -> super.onPreferenceTreeClick(preference)
        }
    }

    private fun stopUpdateLotteryService() {
        requireContext().stopService(
            Intent(
                requireContext(),
                UpdateLotteryIntentService::class.java
            )
        )
    }

    private fun updateAllData() {
        requireContext().startService(
            Intent(
                requireContext(),
                UpdateLotteryIntentService::class.java
            ).apply {
                action = UpdateLotteryIntentService.ACTION_UPDATE_LTO
            }
        )
        requireContext().startService(
            Intent(
                requireContext(),
                UpdateLotteryIntentService::class.java
            ).apply {
                action = UpdateLotteryIntentService.ACTION_UPDATE_LTO_HK
            }
        )
        requireContext().startService(
            Intent(
                requireContext(),
                UpdateLotteryIntentService::class.java
            ).apply {
                action = UpdateLotteryIntentService.ACTION_UPDATE_LTO_BIG
            }
        )
        requireContext().startService(
            Intent(
                requireContext(),
                UpdateLotteryIntentService::class.java
            ).apply {
                action = UpdateLotteryIntentService.ACTION_UPDATE_LTO_LIST3
            }
        )
        requireContext().startService(
            Intent(
                requireContext(),
                UpdateLotteryIntentService::class.java
            ).apply {
                action = UpdateLotteryIntentService.ACTION_UPDATE_LTO_LIST4
            }
        )
    }

    private fun nukeAllTables() {
        repository.nukeLto()
        repository.nukeLtoBig()
        repository.nukeLtoHK()
        repository.nukeResult()
        repository.nukeLtoList3()
        repository.nukeLtoList4()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            KEY_LARGE_TABLE_TEXT_SIZE -> {
                updateLargeTableTextSizeSummary()
                if (activity is SettingsActivity) (activity as SettingsActivity).fragmentResult =
                    Activity.RESULT_OK
            }
            KEY_SMALL_TABLE_TEXT_SIZE -> {
                updateSmallTableTextSizeSummary()
                if (activity is SettingsActivity) (activity as SettingsActivity).fragmentResult =
                    Activity.RESULT_OK
            }
            KEY_LIST_TABLE_TEXT_SIZE -> {
                updateListTableTextSizeSummary()
                if (activity is SettingsActivity) (activity as SettingsActivity).fragmentResult =
                    Activity.RESULT_OK
            }
            KEY_ALWAYS_USE_HORIZONTAL -> {
                if (activity is SettingsActivity) (activity as SettingsActivity).fragmentResult =
                    Activity.RESULT_OK
            }
        }
    }
}