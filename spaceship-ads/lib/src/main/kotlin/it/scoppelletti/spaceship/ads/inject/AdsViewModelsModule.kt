/*
 * Copyright (C) 2018 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship.ads.inject

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import it.scoppelletti.spaceship.ads.lifecycle.ConsentFragmentViewModel
import it.scoppelletti.spaceship.ads.lifecycle.ConsentPrivacyViewModel
import it.scoppelletti.spaceship.ads.lifecycle.ConsentPromptViewModel
import it.scoppelletti.spaceship.ads.lifecycle.ConsentViewModel
import it.scoppelletti.spaceship.html.inject.HtmlModule
import it.scoppelletti.spaceship.inject.CoreViewModelsModule
import it.scoppelletti.spaceship.inject.ViewModelKey

/**
 * Defines the `ViewModel` classes exported by this library.
 *
 * @since 1.0.0
 */
@Module(includes = [ CoreViewModelsModule::class, AdsModule::class,
    HtmlModule::class ])
public abstract class AdsViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(ConsentViewModel::class)
    public abstract fun bindConsentViewModel(
            viewsModel: ConsentViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ConsentFragmentViewModel::class)
    public abstract fun bindConsentFragmentViewModel(
            viewsModel: ConsentFragmentViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ConsentPromptViewModel::class)
    public abstract fun bindConsentPromptViewModel(
            viewsModel: ConsentPromptViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ConsentPrivacyViewModel::class)
    public abstract fun bindConsentPrivacyViewModel(
            viewsModel: ConsentPrivacyViewModel
    ): ViewModel
}