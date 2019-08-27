package it.scoppelletti.spaceship.gms.sample

import android.app.Application
import it.scoppelletti.spaceship.gms.sample.inject.AppComponent
import it.scoppelletti.spaceship.gms.sample.inject.DaggerAppComponent
import it.scoppelletti.spaceship.inject.UIComponent
import it.scoppelletti.spaceship.inject.UIComponentProvider
import it.scoppelletti.spaceship.inject.StdlibComponent
import it.scoppelletti.spaceship.inject.StdlibComponentProvider

class MainApp : Application(), UIComponentProvider,
        StdlibComponentProvider {

    private lateinit var _appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        _appComponent = DaggerAppComponent.factory()
                .create(this)
    }

    override fun uiComponent(): UIComponent = _appComponent

    override fun stdlibComponent(): StdlibComponent = _appComponent
}
