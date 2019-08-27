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

@file:Suppress("RedundantVisibilityModifier", "unused")

package it.scoppelletti.spaceship.inject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import it.scoppelletti.spaceship.ApplicationException
import it.scoppelletti.spaceship.StdlibExt
import it.scoppelletti.spaceship.i18n.AndroidResourceMessageSource
import it.scoppelletti.spaceship.i18n.AndroidResourceMessageSpec
import it.scoppelletti.spaceship.i18n.DefaultI18NProvider
import it.scoppelletti.spaceship.i18n.I18NProvider
import it.scoppelletti.spaceship.i18n.MessageSourceHandler
import it.scoppelletti.spaceship.io.DefaultIOProvider
import it.scoppelletti.spaceship.io.IOProvider
import it.scoppelletti.spaceship.lifecycle.AlertDialogModel
import it.scoppelletti.spaceship.lifecycle.DefaultViewModelFactory
import it.scoppelletti.spaceship.lifecycle.ExceptionDialogModel
import it.scoppelletti.spaceship.widget.ApplicationExceptionMapperHandler
import it.scoppelletti.spaceship.widget.DefaultExceptionMapper
import it.scoppelletti.spaceship.widget.ExceptionMapper
import it.scoppelletti.spaceship.widget.ExceptionMapperHandler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.threeten.bp.Clock
import javax.inject.Named

/**
 * Defines the dependencies provided by this library.
 *
 * @since 1.0.0
 */
@Module(includes = [ ContextModule::class ])
public abstract class UIModule {

    @Binds
    public abstract fun bindIOProvider(obj: DefaultIOProvider): IOProvider

    @Binds
    public abstract fun bindI18NProvider(obj: DefaultI18NProvider): I18NProvider

    @Binds
    @IntoMap
    @MessageSourceKey(AndroidResourceMessageSpec::class)
    public abstract fun bindMessageSourceHandler(
            obj: AndroidResourceMessageSource
    ): MessageSourceHandler<*>

    @Binds
    public abstract fun bindViewModelFactory(
            factory: DefaultViewModelFactory
    ): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(ExceptionDialogModel::class)
    public abstract fun bindExceptionDialogModel(
            viewModel: ExceptionDialogModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AlertDialogModel::class)
    public abstract fun bindAlertDialogModel(
            viewModel: AlertDialogModel
    ): ViewModel

    @Binds
    public abstract fun bindExceptionMapper(
            factory: DefaultExceptionMapper
    ): ExceptionMapper

    @Binds
    @IntoMap
    @ExceptionMapperKey(ApplicationException::class)
    public abstract fun bindApplicationExceptionMapperHandler(
            adapter: ApplicationExceptionMapperHandler
    ): ExceptionMapperHandler<*>

    @Module
    public companion object {

        @Provides
        @JvmStatic
        @Named(StdlibExt.DEP_MAINDISPATCHER)
        public fun provideMainDispatcher(): CoroutineDispatcher =
                Dispatchers.Main

        @Provides
        @JvmStatic
        @Named(StdlibExt.DEP_UTCCLOCK)
        public fun provideUtcClock(): Clock = Clock.systemUTC()
    }
}