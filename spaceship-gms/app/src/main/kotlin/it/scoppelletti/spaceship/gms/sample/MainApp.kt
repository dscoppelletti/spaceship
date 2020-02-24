package it.scoppelletti.spaceship.gms.sample

import android.app.Application
import it.scoppelletti.spaceship.gms.inject.GmsComponent
import it.scoppelletti.spaceship.gms.inject.GmsComponentProvider
import it.scoppelletti.spaceship.gms.sample.inject.AppComponent
import it.scoppelletti.spaceship.gms.sample.inject.DaggerAppComponent
import it.scoppelletti.spaceship.inject.StdlibComponent
import it.scoppelletti.spaceship.inject.UIComponent

@Suppress("unused")
class MainApp : Application(), GmsComponentProvider {

    private lateinit var _appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        _appComponent = DaggerAppComponent.factory()
                .create(this)
    }

    override fun stdlibComponent(): StdlibComponent = _appComponent

    override fun uiComponent(): UIComponent = _appComponent

    override fun gmsComponent(): GmsComponent = _appComponent
}
