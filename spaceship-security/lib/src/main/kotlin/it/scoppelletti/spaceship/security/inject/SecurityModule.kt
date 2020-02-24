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

package it.scoppelletti.spaceship.security.inject

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import it.scoppelletti.spaceship.StdlibExt
import it.scoppelletti.spaceship.inject.UIModule
import it.scoppelletti.spaceship.io.IOProvider
import it.scoppelletti.spaceship.security.CryptoProvider
import it.scoppelletti.spaceship.security.cryptoProvider
import it.scoppelletti.spaceship.security.i18n.DefaultSecurityMessages
import it.scoppelletti.spaceship.security.i18n.SecurityMessages
import org.threeten.bp.Clock
import java.security.SecureRandom
import javax.inject.Named
import javax.inject.Singleton

/**
 * Defines the dependencies exported by this library.
 *
 * @since 1.0.0
 */
@Module(includes = [ UIModule::class ])
public abstract class SecurityModule {

    @Binds
    public abstract fun bindSecurityMessages(
            obj: DefaultSecurityMessages
    ): SecurityMessages

    @Module
    public companion object {

        @Provides
        @Singleton
        @JvmStatic
        public fun provideSecureRandom(): SecureRandom = SecureRandom()

        @Provides
        @JvmStatic
        public fun provideCryptoProvider(
                context: Context,
                ioProvider: IOProvider,
                random: SecureRandom,
                securityMessages: SecurityMessages,

                @Named(StdlibExt.DEP_UTCCLOCK)
                clock: Clock
        ): CryptoProvider =
                cryptoProvider(context, ioProvider, clock, random,
                        securityMessages)
    }
}
