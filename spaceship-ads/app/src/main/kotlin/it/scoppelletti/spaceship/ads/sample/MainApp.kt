package it.scoppelletti.spaceship.ads.sample

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import it.scoppelletti.spaceship.ads.AdsConfig
import it.scoppelletti.spaceship.ads.inject.AdsModule
import it.scoppelletti.spaceship.inject.enableInject
import it.scoppelletti.spaceship.ads.sample.inject.DaggerAppComponent
import javax.inject.Inject

class MainApp : Application(), HasActivityInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()

        AdsModule.registerAdsConfig {
            AdsConfig(BuildConfig.ADS_SERVICEURL,
                    BuildConfig.ADS_PUBLISHERID, BuildConfig.ADS_APPID,
                    listOf(BuildConfig.ADS_UNITID))
        }

        DaggerAppComponent.builder()
                .application(this)
                .build()
                .inject(this)
        enableInject()
    }

    override fun activityInjector(): AndroidInjector<Activity> =
            activityInjector

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
