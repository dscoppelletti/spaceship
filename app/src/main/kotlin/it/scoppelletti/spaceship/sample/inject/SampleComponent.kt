package it.scoppelletti.spaceship.sample.inject

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import it.scoppelletti.spaceship.inject.StdlibComponent
import it.scoppelletti.spaceship.inject.AppComponent
import it.scoppelletti.spaceship.inject.AppModule
import javax.inject.Singleton

@Singleton
@Component(modules = [ AppModule::class ])
interface SampleComponent : StdlibComponent, AppComponent {

    @Component.Factory
    interface Factory {
        fun create(
                @BindsInstance
                application: Application
        ): SampleComponent
    }
}
