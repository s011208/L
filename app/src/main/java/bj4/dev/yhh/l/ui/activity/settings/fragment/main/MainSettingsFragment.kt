package bj4.dev.yhh.l.ui.activity.settings.fragment.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import bj4.dev.yhh.l.R
import bj4.dev.yhh.l.ui.activity.log.LogActivity

class MainSettingsFragment : PreferenceFragmentCompat() {
    companion object {
        private const val KEY_JOB_SERVICE_LOG = "key_job_service_log"
        private const val KEY_UPDATE_TIME_SERVICE_LOG = "key_update_time_service_log"
    }

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
            else -> super.onPreferenceTreeClick(preference)
        }
    }
}