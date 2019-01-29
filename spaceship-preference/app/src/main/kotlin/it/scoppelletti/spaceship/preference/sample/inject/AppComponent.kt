package it.scoppelletti.spaceship.preference.sample.inject

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import it.scoppelletti.spaceship.html.inject.HtmlViewModelsModule
import it.scoppelletti.spaceship.html.inject.HtmlViewsModule
import it.scoppelletti.spaceship.preference.sample.MainApp
import it.scoppelletti.spaceship.inject.ContextModule
import it.scoppelletti.spaceship.inject.CoreViewModelsModule
import it.scoppelletti.spaceship.inject.CoreViewsModule
import it.scoppelletti.spaceship.preference.inject.PreferenceViewModelsModule
import it.scoppelletti.spaceship.preference.inject.PreferenceViewsModule
import javax.inject.Singleton

@Singleton
@Component(modules = [ AndroidInjectionModule::class, ContextModule::class,
    ViewsModule::class, CoreViewsModule::class, CoreViewModelsModule::class,
    HtmlViewsModule::class, HtmlViewModelsModule::class,
    PreferenceViewsModule::class, PreferenceViewModelsModule::class ])
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
