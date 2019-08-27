
@file:Suppress("unused")

package it.scoppelletti.spaceship.sample.inject

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import it.scoppelletti.spaceship.inject.UIComponent
import it.scoppelletti.spaceship.inject.UIModule
import it.scoppelletti.spaceship.inject.StdlibComponent
import it.scoppelletti.spaceship.inject.StdlibModule
import javax.inject.Singleton

@Singleton
@Component(modules = [ AppModule::class, StdlibModule::class, UIModule::class ])
interface AppComponent : StdlibComponent, UIComponent {

    @Component.Factory
    interface Factory {
        fun create(
                @BindsInstance
                application: Application
        ): AppComponent
    }
}
