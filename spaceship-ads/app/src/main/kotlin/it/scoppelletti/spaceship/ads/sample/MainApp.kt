@file:Suppress("JoinDeclarationAndAssignment")

package it.scoppelletti.spaceship.ads.sample

import android.app.Application
import it.scoppelletti.spaceship.ads.AdsConfig
import it.scoppelletti.spaceship.ads.AdsConfigWrapper
import it.scoppelletti.spaceship.ads.inject.AdsComponent
import it.scoppelletti.spaceship.ads.inject.AdsComponentProvider
import it.scoppelletti.spaceship.ads.sample.inject.AppComponent
import it.scoppelletti.spaceship.ads.sample.inject.DaggerAppComponent
import it.scoppelletti.spaceship.inject.StdlibComponent
import it.scoppelletti.spaceship.inject.StdlibComponentProvider
import it.scoppelletti.spaceship.inject.UIComponent
import it.scoppelletti.spaceship.inject.UIComponentProvider

class MainApp : Application(), AdsComponentProvider, StdlibComponentProvider,
        UIComponentProvider {

    private lateinit var _appComponent: AppComponent

    override fun onCreate() {
        val adsConfigWrapper: AdsConfigWrapper

        super.onCreate()

        _appComponent = DaggerAppComponent.factory()
                .create(this)

        adsConfigWrapper = _appComponent.adsConfigWrapper()
        adsConfigWrapper.value =  AdsConfig(BuildConfig.ADS_SERVICEURL,
                BuildConfig.ADS_PUBLISHERID, BuildConfig.ADS_APPID,
                listOf(BuildConfig.ADS_UNITID))
    }

    override fun adsComponent(): AdsComponent = _appComponent

    override fun stdlibComponent(): StdlibComponent = _appComponent

    override fun uiComponent(): UIComponent = _appComponent

    companion object {
        const val PROP_ADS = "it.scoppelletti.spaceship.ads.sample.ads"
    }
}

