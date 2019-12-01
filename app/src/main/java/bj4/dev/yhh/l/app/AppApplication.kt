package bj4.dev.yhh.l.app

import android.app.Application
import bj4.dev.yhh.job_schedulers.UpdateLotteryJobSchedulerService
import bj4.dev.yhh.l.BuildConfig
import bj4.dev.yhh.l.R
import bj4.dev.yhh.l.ui.activity.log.LogActivityViewModel
import bj4.dev.yhh.l.ui.activity.main.MainActivityViewModel
import bj4.dev.yhh.l.ui.activity.main.fragment.large_table.LargeTableViewModel
import bj4.dev.yhh.l.ui.activity.main.fragment.small_table.SmallTableViewModel
import bj4.dev.yhh.l.util.SharedPreferenceHelper
import bj4.dev.yhh.log.LogHelper
import bj4.dev.yhh.repository.database.LotteryDatabaseHelper
import bj4.dev.yhh.repository.repository.LotteryRepository
import bj4.dev.yhh.tracker.TrackHelper
import com.facebook.stetho.Stetho
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber

class AppApplication : Application() {

    private val appModule = module {
        single { LotteryDatabaseHelper(androidContext()) }
        single { LotteryRepository(get()) }
        single { SharedPreferenceHelper(get()) }
        single { LogHelper(get()) }
        single { TrackHelper(androidContext()) }

        viewModel { LargeTableViewModel(get(), get()) }
        viewModel { MainActivityViewModel(get()) }
        viewModel { SmallTableViewModel(get()) }
        viewModel { (type: Int) -> LogActivityViewModel(type, get()) }
    }

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        startKoin {
            androidContext(this@AppApplication)
            modules(appModule)
        }

        if (BuildConfig.DEBUG) {
            Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                    .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                    .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                    .build()
            )
        }

        UpdateLotteryJobSchedulerService.schedule(this@AppApplication)

        FirebaseRemoteConfig.getInstance().setDefaultsAsync(R.xml.firebase_remote_config)
        FirebaseRemoteConfig.getInstance().setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build()
        )
    }
}