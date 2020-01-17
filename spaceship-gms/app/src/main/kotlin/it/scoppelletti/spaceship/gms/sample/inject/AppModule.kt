package it.scoppelletti.spaceship.gms.sample.inject

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import it.scoppelletti.spaceship.gms.inject.GmsModule
import it.scoppelletti.spaceship.gms.sample.MainViewModel
import it.scoppelletti.spaceship.inject.StdlibModule
import it.scoppelletti.spaceship.inject.UIModule
import it.scoppelletti.spaceship.inject.ViewModelKey

@Module(includes = [ GmsModule::class, StdlibModule::class, UIModule::class ])
abstract class AppModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel
}
