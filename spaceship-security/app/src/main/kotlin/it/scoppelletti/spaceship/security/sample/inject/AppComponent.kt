package it.scoppelletti.spaceship.security.sample.inject

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import it.scoppelletti.spaceship.security.inject.SecurityModule
import it.scoppelletti.spaceship.security.sample.MainApp
import javax.inject.Singleton

@Singleton
@Component(modules = [ AndroidInjectionModule::class, ViewsModule::class,
    ViewModelsModule::class, SecurityModule::class ])
interface AppComponent {

    fun inject(app: MainApp)

    @Component.Builder
    interface Builder
    {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}
