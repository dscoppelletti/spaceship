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

package it.scoppelletti.spaceship.gms.inject

import com.google.android.gms.common.api.ApiException
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dagger.multibindings.IntoSet
import it.scoppelletti.spaceship.ExceptionLoggerHandler
import it.scoppelletti.spaceship.gms.FirebaseExceptionLoggerHandler
import it.scoppelletti.spaceship.gms.i18n.DefaultGmsMessages
import it.scoppelletti.spaceship.gms.i18n.GmsMessages
import it.scoppelletti.spaceship.gms.widget.ApiExceptionMapperHandler
import it.scoppelletti.spaceship.inject.ExceptionMapperKey
import it.scoppelletti.spaceship.inject.UIModule
import it.scoppelletti.spaceship.widget.ExceptionMapperHandler

/**
 * Defines the dependencies exported by this library.
 *
 * @since 1.0.0
 */
@Module(includes = [ UIModule::class ])
public abstract class GmsModule {

    @Binds
    public abstract fun bindGmsMessages(
            obj: DefaultGmsMessages
    ): GmsMessages

    @Binds
    @IntoSet
    abstract fun bindExceptionLoggerHandler(
            obj: FirebaseExceptionLoggerHandler
    ): ExceptionLoggerHandler<*>

    @Binds
    @IntoMap
    @ExceptionMapperKey(ApiException::class)
    public abstract fun bindExceptionMapperHandler(
            adapter: ApiExceptionMapperHandler
    ): ExceptionMapperHandler<*>
}
