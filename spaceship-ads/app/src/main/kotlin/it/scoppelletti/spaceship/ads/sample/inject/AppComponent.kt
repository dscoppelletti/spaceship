
@file:Suppress("unused")

package it.scoppelletti.spaceship.ads.sample.inject

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import it.scoppelletti.spaceship.ads.inject.AdsComponent
import it.scoppelletti.spaceship.ads.inject.AdsModule
import it.scoppelletti.spaceship.inject.StdlibComponent
import it.scoppelletti.spaceship.inject.StdlibModule
import it.scoppelletti.spaceship.inject.UIComponent
import it.scoppelletti.spaceship.inject.UIModule
import javax.inject.Singleton

@Singleton
@Component(modules = [ AdsModule::class, StdlibModule::class, UIModule::class ])
interface AppComponent : AdsComponent, StdlibComponent, UIComponent {

    @Component.Factory
    interface Factory {
        fun create(
                @BindsInstance
                application: Application
        ): AppComponent
    }
}

