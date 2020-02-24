package it.scoppelletti.spaceship.preference.sample

import android.app.Application
import it.scoppelletti.spaceship.inject.StdlibComponent
import it.scoppelletti.spaceship.inject.UIComponent
import it.scoppelletti.spaceship.inject.UIComponentProvider
import it.scoppelletti.spaceship.preference.sample.inject.AppComponent
import it.scoppelletti.spaceship.preference.sample.inject.DaggerAppComponent

class MainApp : Application(), UIComponentProvider {

    private lateinit var _appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        _appComponent = DaggerAppComponent.factory()
                .create(this)
    }

    override fun stdlibComponent(): StdlibComponent = _appComponent

    override fun uiComponent(): UIComponent = _appComponent

    companion object {
        const val PROP_CREDITS =
                "it.scoppelletti.spaceship.preference.sample.credits"
        const val PROP_FEEDBACK =
                "it.scoppelletti.spaceship.preference.sample.feedback"
        const val PROP_HELP = "it.scoppelletti.spaceship.preference.sample.help"
    }
}
