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
 * limit
 */

@file:Suppress("RedundantVisibilityModifier")

package it.scoppelletti.spaceship.inject

import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import it.scoppelletti.spaceship.CoreExt
import it.scoppelletti.spaceship.DefaultExceptionLogger
import it.scoppelletti.spaceship.ExceptionLogger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Named

/**
 * Defines the dependencies exported by this library.
 *
 * @since 1.0.0
 */
@Module
public object CoreModule {

    @Provides
    @JvmStatic
    @Named(CoreExt.DEP_MAINDISPATCHER)
    public fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @IntoSet
    @Provides
    @JvmStatic
    public fun provideExceptionLogger(): ExceptionLogger =
            DefaultExceptionLogger()
}
