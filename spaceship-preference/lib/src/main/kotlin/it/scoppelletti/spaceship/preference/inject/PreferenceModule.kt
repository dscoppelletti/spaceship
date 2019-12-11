/*
 * Copyright (C) 2019 Dario Scoppelletti, <http://www.scoppelletti.it/>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("RedundantVisibilityModifier", "unused")

package it.scoppelletti.spaceship.preference.inject

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import it.scoppelletti.spaceship.html.inject.HtmlModule
import it.scoppelletti.spaceship.inject.ContextModule
import it.scoppelletti.spaceship.inject.StdlibModule
import it.scoppelletti.spaceship.inject.UIModule
import it.scoppelletti.spaceship.inject.ViewModelKey
import it.scoppelletti.spaceship.preference.credit.CreditsLoader
import it.scoppelletti.spaceship.preference.credit.DefaultCreditsLoader
import it.scoppelletti.spaceship.preference.i18n.DefaultPreferenceMessages
import it.scoppelletti.spaceship.preference.i18n.PreferenceMessages
import it.scoppelletti.spaceship.preference.lifecycle.CreditsViewModel

/**
 * Defines the dependencies exported by this library.
 *
 * @since 1.0.0
 */
@Module(includes = [ ContextModule::class, HtmlModule::class,
    StdlibModule::class, UIModule::class ])
public abstract class PreferenceModule {

    @Binds
    public abstract fun bindCreditsLoader(
            obj: DefaultCreditsLoader
    ): CreditsLoader

    @Binds
    public abstract fun bindPreferenceMessages(
            obj: DefaultPreferenceMessages
    ): PreferenceMessages

    @Binds
    @IntoMap
    @ViewModelKey(CreditsViewModel::class)
    public abstract fun bindCreditsViewModel(
            viewModel: CreditsViewModel
    ): ViewModel
}
