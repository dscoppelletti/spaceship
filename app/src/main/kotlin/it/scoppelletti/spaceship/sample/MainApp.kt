package it.scoppelletti.spaceship.sample

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import it.scoppelletti.spaceship.inject.StdlibComponent
import it.scoppelletti.spaceship.inject.UIComponent
import it.scoppelletti.spaceship.inject.UIComponentProvider
import it.scoppelletti.spaceship.sample.inject.AppComponent
import it.scoppelletti.spaceship.sample.inject.DaggerAppComponent

@Suppress("unused")
class MainApp : Application(), UIComponentProvider {

    private lateinit var _appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        AndroidThreeTen.init(this)
        _appComponent = DaggerAppComponent.factory()
                .create(this)
    }

    override fun stdlibComponent(): StdlibComponent = _appComponent

    override fun uiComponent(): UIComponent = _appComponent
}



