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

@file:Suppress("RedundantVisibilityModifier", "unused")

package it.scoppelletti.spaceship.http.inject

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import it.scoppelletti.spaceship.http.ClientInterceptor
import it.scoppelletti.spaceship.http.HttpExt
import it.scoppelletti.spaceship.http.widget.HttpExceptionMapperHandler
import it.scoppelletti.spaceship.inject.ExceptionMapperKey
import it.scoppelletti.spaceship.widget.ExceptionMapperHandler
import okhttp3.Interceptor
import retrofit2.HttpException
import javax.inject.Named

/**
 * Defines the dependencies exported by this library.
 *
 * @since 1.0.0
 */
@Module
public abstract class HttpUIModule {

    @Binds
    @Named(HttpExt.DEP_CLIENTINTERCEPTOR)
    public abstract fun bindClientInterceptor(
            obj: ClientInterceptor
    ): Interceptor

    @Binds
    @IntoMap
    @ExceptionMapperKey(HttpException::class)
    public abstract fun bindHttpExceptionMapperHandler(
            adapter: HttpExceptionMapperHandler
    ): ExceptionMapperHandler<*>
}
