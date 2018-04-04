/*
 * Copyright (C) 2008-2017 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship.security;

import java.security.SecureRandom;
import android.support.annotation.NonNull;

/**
 * Operations for security.
 *
 * @since 1.0.0
 */
public final class SecurityExt {

    /**
     * Private constructor for static class.
     */
    private SecurityExt() {
    }

    /**
     * Gets a cryptographically strong random number generator.
     *
     * @return The object.
     */
    @NonNull
    public static SecureRandom getCSRNG() {
        return SecurityExt.SecureRandomHolder.myRNG;
    }

    /**
     * On-demand initialization that does not need synchronization.
     */
    private static final class SecureRandomHolder {
        static final SecureRandom myRNG = new SecureRandom();
    }
}
