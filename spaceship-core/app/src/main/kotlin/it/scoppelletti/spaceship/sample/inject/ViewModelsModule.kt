
@file:Suppress("unused")

package it.scoppelletti.spaceship.sample.inject

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import it.scoppelletti.spaceship.inject.CoreViewModelsModule
import it.scoppelletti.spaceship.inject.ViewModelKey
import it.scoppelletti.spaceship.sample.lifecycle.ItemViewModel
import it.scoppelletti.spaceship.sample.lifecycle.ListViewModel

@Module(includes = [ CoreViewModelsModule::class ])
abstract class ViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(ItemViewModel::class)
    abstract fun bindItemViewModel(viewModel: ItemViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ListViewModel::class)
    abstract fun bindListViewModel(viewModel: ListViewModel): ViewModel
}
