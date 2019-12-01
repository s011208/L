package bj4.dev.yhh.l.ui.activity.settings.fragment.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import bj4.dev.yhh.job_schedulers.UpdateLotteryIntentService
import bj4.dev.yhh.l.R
import bj4.dev.yhh.l.ui.activity.log.LogActivity
import bj4.dev.yhh.l.ui.activity.main.fragment.large_table.LargeTableViewModel
import bj4.dev.yhh.repository.repository.LotteryRepository
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainSettingsFragment : PreferenceFragmentCompat() {
    companion object {
        private const val KEY_JOB_SERVICE_LOG = "key_job_service_log"
        private const val KEY_UPDATE_TIME_SERVICE_LOG = "key_update_time_service_log"

        private const val KEY_RESET_ALL_DATA = "key_reset_all_data"
        private const val KEY_UPDATE_ALL_DATA = "key_update_all_data"
    }

    val repository: LotteryRepository by inject()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_main)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                requireContext().stopService(
                    Intent(
                        requireContext(),
                        UpdateLotteryIntentService::class.java
                    )
                )
                Completable.fromCallable {
                    repository.nukeLto()
                    repository.nukeLtoBig()
                    repository.nukeLtoHK()
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
                                R.string.settings_reset_all_data_toast,
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
                requireContext().stopService(
                    Intent(
                        requireContext(),
                        UpdateLotteryIntentService::class.java
                    )
                )
                updateAllData()
                true
            }
            else -> super.onPreferenceTreeClick(preference)
        }
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
    }
}