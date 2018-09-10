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
     * Name of the `File` dependency containing the absolute path to the
     * directory on the filesystem where all private files belonging to this app
     * are stored.
     * On device runnning `LOLLIPOP` or later, the files placed under this
     * directory will be excluded from automatic backup to remote storage.
     */
    public const val DEP_NOBACKUPFILESDIR: String =
            "it.scoppelletti.spaceship.1"

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
     * Tag of `AlertDialogFragment` fragment.
     *
     * @see it.scoppelletti.spaceship.app.AlertDialogFragment
     */
    public const val TAG_ALERTDIALOG: String = "it.scoppelletti.spaceship.1"

    /**
     * Tag of `ExceptionDialogFragment` fragment.
     *
     * @see it.scoppelletti.spaceship.app.ExceptionDialogFragment
     */
    public const val TAG_EXCEPTIONDIALOG: String = "it.scoppelletti.spaceship.2"

    /**
     * Tag of `BottomSheetDialogFragmentEx` fragment.
     *
     * @see it.scoppelletti.spaceship.app.BottonSheetDialogFragmentEx
     */
    public const val TAG_BOTTOMSHEETDIALOG: String =
            "it.scoppelletti.spaceship.3"
}
