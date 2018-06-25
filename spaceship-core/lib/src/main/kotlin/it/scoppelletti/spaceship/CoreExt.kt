/*
 * Copyright (C) 2013-2017 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship

/**
 * Core extensions.
 *
 * @since 1.0.0
 */
public object CoreExt {

    /**
     * Property indicating whether the user has manually opened the drawer
     * at least once.
     */
    public const val PROP_LEARNED: String = "it.scoppelletti.spaceship.1"

    /**
     * Property containing the title of an activity as a string resource ID.
     */
    public const val PROP_TITLE: String = "it.scoppelletti.spaceship.2"

    /**
     * Tag of `ConfirmDialogFragment` fragment.
     *
     * @see it.scoppelletti.spaceship.app.ConfirmDialogFragment
     */
    public const val TAG_CONFIRMDIALOG: String = "it.scoppelletti.spaceship.1"

    /**
     * Tag of `ExceptionDialogFragment` fragment.
     *
     * @see it.scoppelletti.spaceship.app.ExceptionDialogFragment
     */
    public const val TAG_EXCEPTIONDIALOG: String = "it.scoppelletti.spaceship.2"
}
