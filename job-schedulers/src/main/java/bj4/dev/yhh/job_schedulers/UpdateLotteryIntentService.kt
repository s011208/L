package bj4.dev.yhh.job_schedulers

import android.app.IntentService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import bj4.dev.yhh.log.LogHelper
import bj4.dev.yhh.log.room.entity.UpdateServiceTimeEntity
import bj4.dev.yhh.repository.repository.LotteryRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import org.koin.android.ext.android.inject
import timber.log.Timber

class UpdateLotteryIntentService : IntentService("Update-Lottery") {

    companion object {
        private const val UPDATE_LOTTERY_SERVICE_NOTIFICATION_ID = 6000001

        private const val CHANNEL_ID = "Update Lottery Service"

        private const val SHARED_PREFERENCE_NAME = "pref_update-lottery"
        private const val KEY_HAS_CREATE_NOTIFICATION_CHANNEL =
            "KEY_HAS_CREATE_NOTIFICATION_CHANNEL"

        const val ACTION_UPDATE_LTO_HK = "lto_hk"
        const val ACTION_UPDATE_LTO_BIG = "lto_big"
        const val ACTION_UPDATE_LTO = "lto"
    }

    val logHelper: LogHelper by inject()
    val repository: LotteryRepository by inject()

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate() {
        super.onCreate()
        if (!getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
                .getBoolean(
                    KEY_HAS_CREATE_NOTIFICATION_CHANNEL,
                    false
                )
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel()
                getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).edit()
                    .putBoolean(KEY_HAS_CREATE_NOTIFICATION_CHANNEL, true).apply()
            }
        }

        startForeground(UPDATE_LOTTERY_SERVICE_NOTIFICATION_ID, generateNotification())
    }

    private fun generateNotification(): Notification {
        return NotificationCompat
            .Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alpha_l_circle_outline_white_24dp)
            .setContentTitle(getString(R.string.notification_title))
            .setProgress(100, 100, true)
            .build()
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = getSystemService(
            NOTIFICATION_SERVICE
        ) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) return

        logHelper.insert(
            UpdateServiceTimeEntity(
                message = intent.action ?: "unknown"
            )
        )

        when (intent.action) {
            ACTION_UPDATE_LTO_HK -> {
                Timber.v("ACTION_UPDATE_LTO_HK")
                compositeDisposable += repository.getLtoHK().subscribe(
                    {},
                    {
                        logHelper.insert(
                            UpdateServiceTimeEntity(
                                message = "ACTION_UPDATE_LTO_HK failed, $it"
                            )
                        )
                    },
                    {
                        logHelper.insert(
                            UpdateServiceTimeEntity(
                                message = "ACTION_UPDATE_LTO_HK complete"
                            )
                        )
                    }
                )
            }
            ACTION_UPDATE_LTO_BIG -> {
                Timber.v("ACTION_UPDATE_LTO_BIG")
                compositeDisposable += repository.getLtoHK().subscribe(
                    {},
                    {
                        logHelper.insert(
                            UpdateServiceTimeEntity(
                                message = "ACTION_UPDATE_LTO_BIG failed, $it"
                            )
                        )
                    },
                    {
                        logHelper.insert(
                            UpdateServiceTimeEntity(
                                message = "ACTION_UPDATE_LTO_BIG complete"
                            )
                        )
                    }
                )
            }
            ACTION_UPDATE_LTO -> {
                Timber.v("ACTION_UPDATE_LTO")
                compositeDisposable += repository.getLtoHK().subscribe(
                    {},
                    {
                        logHelper.insert(
                            UpdateServiceTimeEntity(
                                message = "ACTION_UPDATE_LTO failed, $it"
                            )
                        )
                    },
                    {
                        logHelper.insert(
                            UpdateServiceTimeEntity(
                                message = "ACTION_UPDATE_LTO complete"
                            )
                        )
                    }
                )
            }
        }
    }
}