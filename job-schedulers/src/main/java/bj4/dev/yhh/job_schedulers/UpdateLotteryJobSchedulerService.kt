package bj4.dev.yhh.job_schedulers

import android.app.AlarmManager
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import bj4.dev.yhh.log.LogHelper
import bj4.dev.yhh.log.room.entity.JobServiceEntity
import org.koin.android.ext.android.inject
import timber.log.Timber

class UpdateLotteryJobSchedulerService : JobService() {
    companion object {
        private const val JOB_ID = 1000001

        fun schedule(context: Context) {
            val jobScheduler =
                context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val job = JobInfo.Builder(
                JOB_ID,
                ComponentName(context, UpdateLotteryJobSchedulerService::class.java)
            )
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .setPeriodic(AlarmManager.INTERVAL_HOUR * 3)
                .setPersisted(true)
                .build()

            val result = jobScheduler.schedule(job)
            Timber.v("UpdateLotteryJobSchedulerService scheduled, result: $result")
        }
    }

    val logHelper: LogHelper by inject()

    override fun onStopJob(params: JobParameters): Boolean {
        Timber.v("UpdateLotteryJobSchedulerService onStopJob")
        logHelper.insert(JobServiceEntity(message = "onStopJob"))
        return false
    }

    override fun onStartJob(params: JobParameters): Boolean {
        startService(Intent(this, UpdateLotteryIntentService::class.java).apply {
            action = UpdateLotteryIntentService.ACTION_UPDATE_LTO_HK
        })
        startService(Intent(this, UpdateLotteryIntentService::class.java).apply {
            action = UpdateLotteryIntentService.ACTION_UPDATE_LTO_BIG
        })
        startService(Intent(this, UpdateLotteryIntentService::class.java).apply {
            action = UpdateLotteryIntentService.ACTION_UPDATE_LTO
        })

        Timber.v("UpdateLotteryJobSchedulerService onStartJob")
        logHelper.insert(JobServiceEntity(message = "onStartJob"))
        return false
    }
}