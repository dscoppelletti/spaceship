@file:Suppress("unused")

package it.scoppelletti.spaceship.preference.sample.inject

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import it.scoppelletti.spaceship.inject.StdlibComponent
import it.scoppelletti.spaceship.inject.UIComponent
import it.scoppelletti.spaceship.preference.inject.PreferenceModule
import javax.inject.Singleton

@Singleton
@Component(modules = [ PreferenceModule::class ])
interface AppComponent : StdlibComponent, UIComponent {

    @Component.Factory
    interface Factory {
        fun create(
                @BindsInstance
                application: Application
        ): AppComponent
    }
}
