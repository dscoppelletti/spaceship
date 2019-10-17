package it.scoppelletti.spaceship.security.sample.inject

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import it.scoppelletti.spaceship.inject.StdlibComponent
import it.scoppelletti.spaceship.inject.StdlibModule
import it.scoppelletti.spaceship.inject.UIComponent
import it.scoppelletti.spaceship.inject.UIModule
import it.scoppelletti.spaceship.security.inject.SecurityModule
import javax.inject.Singleton

@Singleton
@Component(modules = [ AppModule::class, SecurityModule::class,
    StdlibModule::class, UIModule::class ])
interface AppComponent : StdlibComponent, UIComponent {

    @Component.Factory
    interface Factory {
        fun create(
                @BindsInstance
                application: Application
        ): AppComponent
    }
}