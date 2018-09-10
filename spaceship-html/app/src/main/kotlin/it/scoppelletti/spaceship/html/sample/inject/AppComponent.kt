package it.scoppelletti.spaceship.html.sample.inject

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import it.scoppelletti.spaceship.html.inject.HtmlViewModelsModule
import it.scoppelletti.spaceship.html.inject.HtmlViewsModule
import it.scoppelletti.spaceship.html.sample.MainApp
import it.scoppelletti.spaceship.inject.ContextModule
import it.scoppelletti.spaceship.inject.CoreViewModelsModule
import javax.inject.Singleton

@Singleton
@Component(modules = [ AndroidInjectionModule::class,
    ContextModule::class, CoreViewModelsModule::class, HtmlViewsModule::class,
    HtmlViewModelsModule::class ])
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
