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

package it.scoppelletti.spaceship.inject

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dagger.multibindings.IntoSet
import it.scoppelletti.spaceship.DefaultExceptionLogger
import it.scoppelletti.spaceship.DefaultExceptionLoggerHandler
import it.scoppelletti.spaceship.ExceptionLogger
import it.scoppelletti.spaceship.ExceptionLoggerHandler
import it.scoppelletti.spaceship.i18n.DefaultMessageSource
import it.scoppelletti.spaceship.i18n.MessageSource
import it.scoppelletti.spaceship.i18n.MessageSourceHandler
import it.scoppelletti.spaceship.i18n.ResourceBundleMessageSource
import it.scoppelletti.spaceship.i18n.ResourceBundleMessageSpec

/**
 * Defines the dependencies provided by this library.
 *
 * @since 1.0.0
 */
@Module
public abstract class StdlibModule {

    @Binds
    public abstract fun bindMessageSource(
            obj: DefaultMessageSource
    ): MessageSource

    @Binds
    @IntoMap
    @MessageSourceKey(ResourceBundleMessageSpec::class)
    public abstract fun bindMessageSourceHandler(
            obj: ResourceBundleMessageSource
    ): MessageSourceHandler<*>

    @Binds
    public abstract fun bindExceptionLogger(
            obj: DefaultExceptionLogger
    ): ExceptionLogger

    @Binds
    @IntoSet
    public abstract fun bindExceptionLoggerHandler(
            obj: DefaultExceptionLoggerHandler
    ): ExceptionLoggerHandler<*>
}
