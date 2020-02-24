@file:Suppress("unused")

package it.scoppelletti.spaceship.gms.sample.inject

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import it.scoppelletti.spaceship.gms.inject.GmsComponent
import it.scoppelletti.spaceship.gms.inject.GmsModule
import it.scoppelletti.spaceship.inject.StdlibComponent
import it.scoppelletti.spaceship.inject.UIComponent
import javax.inject.Singleton

@Singleton
@Component(modules = [ GmsModule::class ])
interface AppComponent : GmsComponent, StdlibComponent, UIComponent {

    @Component.Factory
    interface Factory {
        fun create(
                @BindsInstance
                application: Application
        ): AppComponent
    }
}
