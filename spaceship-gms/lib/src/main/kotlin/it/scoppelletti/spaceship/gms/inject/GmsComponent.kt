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

@file:Suppress("RedundantVisibilityModifier")

package it.scoppelletti.spaceship.gms.inject

import it.scoppelletti.spaceship.gms.i18n.GmsMessages
import it.scoppelletti.spaceship.inject.UIComponentProvider

/**
 * Access to the dependencies provided by this library.
 *
 * @since 1.0.0
 */
public interface GmsComponent {

    fun gmsMessages(): GmsMessages
}

/**
 * Provides the `GmsComponent` component.
 *
 * @since 1.0.0
 */
public interface GmsComponentProvider : UIComponentProvider {

    fun gmsComponent(): GmsComponent
}


