package it.scoppelletti.spaceship.sample

import android.app.Application
import it.scoppelletti.spaceship.inject.AppComponent
import it.scoppelletti.spaceship.inject.StdlibComponent
import it.scoppelletti.spaceship.inject.AppComponentProvider
import it.scoppelletti.spaceship.sample.inject.DaggerSampleComponent
import it.scoppelletti.spaceship.sample.inject.SampleComponent

@Suppress("unused")
class MainApp : Application(), AppComponentProvider {

    private lateinit var _sampleComponent: SampleComponent

    override fun onCreate() {
        super.onCreate()

        _sampleComponent = DaggerSampleComponent.factory()
                .create(this)
    }

    override fun stdlibComponent(): StdlibComponent = _sampleComponent

    override fun appComponent(): AppComponent = _sampleComponent
}



