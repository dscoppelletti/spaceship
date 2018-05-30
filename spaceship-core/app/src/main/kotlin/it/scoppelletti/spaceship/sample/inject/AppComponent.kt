package it.scoppelletti.spaceship.sample.inject

import dagger.Component
import dagger.android.AndroidInjectionModule
import it.scoppelletti.spaceship.sample.MainApp
import javax.inject.Singleton

@Singleton
@Component(modules = [ AndroidInjectionModule::class, ViewsModule::class,
    ViewModelsModule::class, AppModule::class ])
interface AppComponent {
    fun inject(app: MainApp)
}