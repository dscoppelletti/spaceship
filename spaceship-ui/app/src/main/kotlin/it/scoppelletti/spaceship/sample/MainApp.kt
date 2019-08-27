package it.scoppelletti.spaceship.sample

import android.app.Application
import it.scoppelletti.spaceship.inject.UIComponent
import it.scoppelletti.spaceship.inject.UIComponentProvider
import it.scoppelletti.spaceship.inject.StdlibComponent
import it.scoppelletti.spaceship.inject.StdlibComponentProvider
import it.scoppelletti.spaceship.sample.inject.AppComponent
import it.scoppelletti.spaceship.sample.inject.DaggerAppComponent

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

    companion object {
        const val PROP_ITEMID = "it.scoppelletti.spaceship.sample.1"
        const val TAG_DELETEDLG = "it.scoppelletti.spaceship.sample.2"
        const val TAG_SAVEDLG = "it.scoppelletti.spaceship.sample.3"
    }
}
