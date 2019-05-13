
@file:Suppress("unused")

package it.scoppelletti.spaceship.sample.inject

import dagger.Component
import dagger.android.AndroidInjectionModule
import it.scoppelletti.spaceship.inject.CoreViewsModule
import it.scoppelletti.spaceship.sample.MainApp
import javax.inject.Singleton

@Singleton
@Component(modules = [ AndroidInjectionModule::class, ViewsModule::class,
    ViewModelsModule::class, CoreViewsModule::class, AppModule::class ])
interface AppComponent {

    fun inject(app: MainApp)
}