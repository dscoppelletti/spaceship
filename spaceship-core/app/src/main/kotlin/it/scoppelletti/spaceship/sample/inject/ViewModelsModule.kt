package it.scoppelletti.spaceship.sample.inject

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import it.scoppelletti.spaceship.inject.InjectViewModelFactory
import it.scoppelletti.spaceship.inject.ViewModelKey
import it.scoppelletti.spaceship.sample.viewmodel.ItemViewModel
import it.scoppelletti.spaceship.sample.viewmodel.ListViewModel

@Module
abstract class ViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(ItemViewModel::class)
    abstract fun bindItemViewModel(viewModel: ItemViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ListViewModel::class)
    abstract fun bindListViewModel(viewModel: ListViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(
            factory: InjectViewModelFactory
    ): ViewModelProvider.Factory
}
