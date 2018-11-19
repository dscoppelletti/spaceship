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

package it.scoppelletti.spaceship.security.inject

import android.content.Context
import dagger.Module
import dagger.Provides
import it.scoppelletti.spaceship.inject.ContextModule
import it.scoppelletti.spaceship.inject.IOModule
import it.scoppelletti.spaceship.inject.TimeModule
import it.scoppelletti.spaceship.io.IOProvider
import it.scoppelletti.spaceship.security.CryptoProvider
import it.scoppelletti.spaceship.security.cryptoProvider
import it.scoppelletti.spaceship.types.TimeProvider
import java.security.SecureRandom
import javax.inject.Singleton

/**
 * Defines the dependencies exported by this library.
 *
 * @since 1.0.0
 */
@Module(includes = [ ContextModule::class, IOModule::class, TimeModule::class ])
public object SecurityModule {

    @Provides
    @Singleton
    @JvmStatic
    public fun provideSecureRandom(): SecureRandom = SecureRandom()

    @Provides
    @JvmStatic
    public fun provideCryptoProvider(
            context: Context,
            ioProvider: IOProvider,
            timeProvider: TimeProvider,
            random: SecureRandom
    ): CryptoProvider =
            cryptoProvider(context, ioProvider, timeProvider, random)
}
