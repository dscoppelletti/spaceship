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

@file:Suppress("RedundantVisibilityModifier")

package it.scoppelletti.spaceship

/**
 * Standard Library extensions.
 *
 * @since 1.0.0
 */
@Suppress("unused")
public object StdlibExt {

    /**
     * Name of the main `CoroutineDispatcher` dependency.
     */
    public const val DEP_MAINDISPATCHER = "it.scoppelletti.spaceship.stdlib.1"

    /**
     * Name of the `Clock` dependency for accessing to the current instant,
     * converting to date and time using the UTC time-zone.
     */
    public const val DEP_UTCCLOCK = "it.scoppelletti.spaceship.stdlib.2"
}
