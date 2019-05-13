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

package it.scoppelletti.spaceship.widget

import android.view.View
import android.view.ViewGroup

/**
 * Renders an exception as an item in a `ListView` widget.
 *
 * @param T The exception class.
 * @since   1.0.0
 */
public interface ExceptionAdapter<in T : Throwable> {

    /**
     * Get a view that displays the data of an exception.
     *
     * @param ex     Exception that shoud be rendered.
     * @param parent The parent that the returned view will be eventually
     *               attached to.
     */
    fun getView(ex: T, parent: ViewGroup): View

    /**
     * Creates an adapter for an exception class.
     *
     * @since 1.0.0
     */
    public interface Factory {

        /**
         * Creates an adapter for an exception.
         *
         * @param  exClass Exception class.
         * @return         The new object.
         */
        fun <T : Throwable> create(exClass: Class<T>): ExceptionAdapter<*>
    }
}
