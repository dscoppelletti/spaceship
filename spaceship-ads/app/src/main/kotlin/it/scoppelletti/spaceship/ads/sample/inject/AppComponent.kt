package it.scoppelletti.spaceship.ads.sample.inject

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import it.scoppelletti.spaceship.ads.inject.AdsViewModelsModule
import it.scoppelletti.spaceship.ads.sample.MainApp
import it.scoppelletti.spaceship.inject.CoreViewsModule
import javax.inject.Singleton

@Singleton
@Component(modules = [ AndroidInjectionModule::class, ViewsModule::class,
    CoreViewsModule::class, AdsViewModelsModule::class ])
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
