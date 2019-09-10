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

package it.scoppelletti.spaceship.i18n

import it.scoppelletti.spaceship.R

/**
 * String resources.
 *
 * @since 1.0.0
 */
public object UIMessages {

    public fun errorPackageNotFound(name: String): MessageSpec =
            AndroidResourceMessageSpec(
                    R.string.it_scoppelletti_err_packageNotFound,
                    "it_scoppelletti_err_packageNotFound", arrayOf(name))

    public fun promptSaveChanges(): MessageSpec =
            AndroidResourceMessageSpec(
                    R.string.it_scoppelletti_prompt_saveChanges,
                    "it_scoppelletti_prompt_saveChanges")
}