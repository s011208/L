package bj4.dev.yhh.job_schedulers

import android.app.IntentService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import bj4.dev.yhh.log.LogHelper
import bj4.dev.yhh.log.room.entity.UpdateServiceTimeEntity
import bj4.dev.yhh.repository.LotteryType
import bj4.dev.yhh.repository.repository.LotteryRepository
import bj4.dev.yhh.tracker.TrackHelper
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.net.UnknownHostException

class UpdateLotteryIntentService : IntentService("Update-Lottery") {

    companion object {
        private const val UPDATE_LOTTERY_SERVICE_NOTIFICATION_ID = 6000001

        private const val CHANNEL_ID = "Update Lottery Service"

        private const val SHARED_PREFERENCE_NAME = "pref_update-lottery"
        private const val KEY_HAS_CREATE_NOTIFICATION_CHANNEL =
            "KEY_HAS_CREATE_NOTIFICATION_CHANNEL"

        const val ACTION_UPDATE_LTO_LIST3 = "lto_list3"
        const val ACTION_UPDATE_LTO_LIST4 = "lto_list4"
        const val ACTION_UPDATE_LTO_HK = "lto_hk"
        const val ACTION_UPDATE_LTO_BIG = "lto_big"
        const val ACTION_UPDATE_LTO = "lto"
    }

    private val logHelper: LogHelper by inject()
    private val trackHelper: TrackHelper by inject()
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

    private fun handleError(throwable: Throwable) {
        if (throwable is UnknownHostException) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(
                    applicationContext,
                    R.string.error_message_no_internet,
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(
                    applicationContext,
                    R.string.error_message_unknown,
                    Toast.LENGTH_LONG
                ).show()
            }

            trackHelper.trackEvent(TrackHelper.TRACK_NAME_PARSER_ERROR, Bundle().apply {
                putString(TrackHelper.PARAM_ERROR, throwable.message)
            })
        }
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
                compositeDisposable += repository.parseLotteryData(LotteryType.LtoHK)
                    .subscribe(
                        {
                            Timber.v("ACTION_UPDATE_LTO_HK, page: $it")
                        },
                        {
                            logHelper.insert(
                                UpdateServiceTimeEntity(
                                    message = "ACTION_UPDATE_LTO_HK failed, $it"
                                )
                            )
                            Timber.w(it, "ACTION_UPDATE_LTO_HK")
                            handleError(it)
                        },
                        {
                            Timber.v("ACTION_UPDATE_LTO_HK, complete")
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
                compositeDisposable += repository.parseLotteryData(LotteryType.LtoBig)
                    .subscribe(
                        {
                            Timber.v("ACTION_UPDATE_LTO_BIG, page: $it")
                        },
                        {
                            logHelper.insert(
                                UpdateServiceTimeEntity(
                                    message = "ACTION_UPDATE_LTO_BIG failed, $it"
                                )
                            )
                            Timber.w(it, "ACTION_UPDATE_LTO_BIG")
                            handleError(it)
                        },
                        {
                            Timber.v("ACTION_UPDATE_LTO_BIG, complete")
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
                compositeDisposable += repository.parseLotteryData(LotteryType.Lto)
                    .subscribe(
                        {
                            Timber.v("ACTION_UPDATE_LTO, page: $it")
                        },
                        {
                            logHelper.insert(
                                UpdateServiceTimeEntity(
                                    message = "ACTION_UPDATE_LTO failed, $it"
                                )
                            )
                            Timber.w(it, "ACTION_UPDATE_LTO")
                            handleError(it)
                        },
                        {
                            Timber.v("ACTION_UPDATE_LTO, complete")
                            logHelper.insert(
                                UpdateServiceTimeEntity(
                                    message = "ACTION_UPDATE_LTO complete"
                                )
                            )
                        }
                    )
            }
            ACTION_UPDATE_LTO_LIST3 -> {
                Timber.v("ACTION_UPDATE_LTO_LIST3")
                compositeDisposable += repository.parseLotteryData(LotteryType.LtoList3)
                    .subscribe(
                        {
                            Timber.v("ACTION_UPDATE_LTO_LIST3, page: $it")
                        },
                        {
                            logHelper.insert(
                                UpdateServiceTimeEntity(
                                    message = "ACTION_UPDATE_LTO_LIST3 failed, $it"
                                )
                            )
                            Timber.w(it, "ACTION_UPDATE_LTO_LIST3")
                            handleError(it)
                        },
                        {
                            Timber.v("ACTION_UPDATE_LTO_LIST3, complete")
                            logHelper.insert(
                                UpdateServiceTimeEntity(
                                    message = "ACTION_UPDATE_LTO_LIST3 complete"
                                )
                            )
                        }
                    )
            }
            ACTION_UPDATE_LTO_LIST4 -> {
                Timber.v("ACTION_UPDATE_LTO_LIST3")
                compositeDisposable += repository.parseLotteryData(LotteryType.LtoList4)
                    .subscribe(
                        {
                            Timber.v("ACTION_UPDATE_LTO_LIST4, page: $it")
                        },
                        {
                            logHelper.insert(
                                UpdateServiceTimeEntity(
                                    message = "ACTION_UPDATE_LTO_LIST4 failed, $it"
                                )
                            )
                            Timber.w(it, "ACTION_UPDATE_LTO_LIST4")
                            handleError(it)
                        },
                        {
                            Timber.v("ACTION_UPDATE_LTO_LIST4, complete")
                            logHelper.insert(
                                UpdateServiceTimeEntity(
                                    message = "ACTION_UPDATE_LTO_LIST4 complete"
                                )
                            )
                        }
                    )
            }
        }
    }
}