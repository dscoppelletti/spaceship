@file:Suppress("unused")

package it.scoppelletti.spaceship.gms.sample.inject

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import it.scoppelletti.spaceship.inject.UIComponent
import it.scoppelletti.spaceship.inject.UIModule
import it.scoppelletti.spaceship.inject.StdlibComponent
import it.scoppelletti.spaceship.inject.StdlibModule
import javax.inject.Singleton

@Singleton
@Component(modules = [ StdlibModule::class, UIModule::class ])
interface AppComponent : UIComponent, StdlibComponent {

    @Component.Factory
    interface Factory {
        fun create(
                @BindsInstance
                application: Application
        ): AppComponent
    }
}