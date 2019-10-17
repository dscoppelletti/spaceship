package it.scoppelletti.spaceship.security.sample

import android.app.Application
import it.scoppelletti.spaceship.inject.StdlibComponent
import it.scoppelletti.spaceship.inject.StdlibComponentProvider
import it.scoppelletti.spaceship.inject.UIComponent
import it.scoppelletti.spaceship.inject.UIComponentProvider
import it.scoppelletti.spaceship.security.sample.inject.AppComponent
import it.scoppelletti.spaceship.security.sample.inject.DaggerAppComponent

@Suppress("unused")
class MainApp : Application(), StdlibComponentProvider, UIComponentProvider {

    private lateinit var _appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        _appComponent = DaggerAppComponent.factory()
                .create(this)
    }

    override fun stdlibComponent(): StdlibComponent = _appComponent

    override fun uiComponent(): UIComponent = _appComponent
}


