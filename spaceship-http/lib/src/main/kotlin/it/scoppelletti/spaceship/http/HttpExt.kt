/*
 * Copyright (C) 2017 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship.http

/**
 * Operations for HTTP protocol.
 *
 * @since 1.0.0
 */
public object HttpExt {

    /**
     * Authorization type `Bearer`.
     */
    public const val AUTH_BEARER: String = "Bearer"

    /**
     * Header containing the client application name and version.
     */
    public const val HEADER_APPL: String = "X-scoppelletti-appl"

    /**
     * Header `Authorization`.
     */
    public const val HEADER_AUTH: String = "Authorization"

    /**
     * Header `Accept-Language`.
     */
    public const val HEADER_LOCALE: String = "Accept-Language"

    /**
     * Header containing the client OS name and version.
     */
    public const val HEADER_OS: String = "X-scoppelletti-os"
}
