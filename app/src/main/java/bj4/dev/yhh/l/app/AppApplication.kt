package bj4.dev.yhh.l.app

import android.app.Application
import bj4.dev.yhh.l.BuildConfig
import bj4.dev.yhh.l.ui.fragment.lto.LtoViewModel
import bj4.dev.yhh.l.ui.fragment.lto_big.LtoBigViewModel
import bj4.dev.yhh.l.ui.fragment.lto_hk.LtoHKViewModel
import bj4.dev.yhh.l.util.SharedPreferenceHelper
import bj4.dev.yhh.repository.database.LotteryDatabaseHelper
import bj4.dev.yhh.repository.repository.LotteryRepository
import com.facebook.stetho.Stetho
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

        viewModel { LtoHKViewModel(get(), get()) }
        viewModel { LtoBigViewModel(get(), get()) }
        viewModel { LtoViewModel(get(), get()) }
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
    }
}