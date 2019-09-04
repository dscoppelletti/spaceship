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

@file:Suppress("RedundantVisibilityModifier", "UNUSED")

package it.scoppelletti.spaceship.gms

import com.crashlytics.android.Crashlytics
import it.scoppelletti.spaceship.ExceptionLoggerHandler
import javax.inject.Inject

/**
 * Implementation of the `ExceptionLoggerHandler` interface using Firebase
 * Crashlytics.
 *
 * It is up to you to enable `FirebaseExceptionLoggerHandler` binding it to your
 * Dagger graph.
 *
 *     @Module
 *     abstract class AppModule {
 *
 *         @Binds
 *         @IntoSet
 *         abstract fun bindExceptionLoggerHandler(
 *             obj: FirebaseExceptionLoggerHandler
 *         ): ExceptionLoggerHandler<*>
 *     )
 *
 * * [Firebase Crashlytics](http://firebase.google.com/products/crashlytics)
 * * [Dagger 2](http://dagger.dev)
 */
public class FirebaseExceptionLoggerHandler @Inject constructor(
): ExceptionLoggerHandler<Throwable> {

    override fun log(ex: Throwable) {
        Crashlytics.logException(ex)
    }
}