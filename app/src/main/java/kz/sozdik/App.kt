package kz.sozdik

import android.content.ContextWrapper
import android.os.Build
import android.os.StrictMode
import androidx.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.facebook.drawee.backends.pipeline.Fresco
import com.pixplicity.easyprefs.library.Prefs
import io.fabric.sdk.android.Fabric
import kz.sozdik.core.di.AppDependency
import kz.sozdik.di.ApplicationComponent
import kz.sozdik.di.DaggerApplicationComponent
import timber.log.Timber

class App : MultiDexApplication(), AppDependency.AppDepsProvider {

    private val appComponent: ApplicationComponent by lazy {
        DaggerApplicationComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        setupDependencies()
        setupStrictMode()
        setupFresco()
        setupCrashlytics()
        setupTimber()
        setupEasyPrefs()
    }

    override fun provideAppDependency(): AppDependency = appComponent

    private fun setupDependencies() {
        appComponent.inject(this)
    }

    private fun setupFresco() {
        Fresco.initialize(this)
    }

    private fun setupCrashlytics() {
        val core = CrashlyticsCore.Builder()
            .disabled(BuildConfig.DEBUG)
            .build()
        val crashlytics = Crashlytics.Builder()
            .core(core)
            .build()
        Fabric.with(baseContext, crashlytics)
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun setupEasyPrefs() {
        Prefs.Builder()
            .setContext(this)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()
    }

    private fun setupStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .permitDiskWrites()
                    .penaltyFlashScreen()
                    .penaltyDeathOnNetwork()
                    .apply {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            detectResourceMismatches()
                        }
                    }
                    .build()
            )
        }
    }
}