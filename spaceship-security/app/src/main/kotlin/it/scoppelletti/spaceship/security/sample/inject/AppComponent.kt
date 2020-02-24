package it.scoppelletti.spaceship.security.sample.inject

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import it.scoppelletti.spaceship.inject.StdlibComponent
import it.scoppelletti.spaceship.inject.UIComponent
import javax.inject.Singleton

@Singleton
@Component(modules = [ AppModule::class ])
interface AppComponent : StdlibComponent, UIComponent {

    @Component.Factory
    interface Factory {
        fun create(
                @BindsInstance
                application: Application
        ): AppComponent
    }
}

