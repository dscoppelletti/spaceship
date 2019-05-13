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

package it.scoppelletti.spaceship.preference

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.UiThread
import androidx.preference.DialogPreference

/**
 * Custom setting with an editor dialog [CustomPreferenceDialogFragment].
 *
 * @since 1.0.0
 */
@UiThread
public abstract class CustomDialogPreference : DialogPreference {

    /**
     * @constructor         Constructor.
     * @param       context Context.
     */
    @Suppress("unused")
    public constructor(context: Context) : super(context)

    /**
     * @constructor         Constructor.
     * @param       context Context.
     * @param       attrs   Attribute set.
     */
    @Suppress("unused")
    public constructor(
            context: Context,
            attrs: AttributeSet
    ) : super(context, attrs)

    /**
     * @constructor              Constructor.
     * @param       context      Context.
     * @param       attrs        Attribute set.
     * @param       defStyleAttr Default style attributes as a resource ID.
     */
    @Suppress("unused")
    public constructor(
            context: Context,
            attrs: AttributeSet,
            defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    /**
     * @constructor              Constructor.
     * @param       context      Context.
     * @param       attrs        Attribute set.
     * @param       defStyleAttr Default style attributes as a resource ID.
     * @param       defStyleRes  Default style as a resource ID.
     */
    @Suppress("unused")
    public constructor(
            context: Context,
            attrs: AttributeSet,
            defStyleAttr: Int,
            defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    /**
     * Binds views in the content view of the dialog to data.
     *
     * @param view Content view.
     */
    internal fun doBindDialogView(view: View) = onBindDialogView(view)

    /**
     * Binds views in the content view of the dialog to data.
     *
     * @param view Content view.
     */
    protected abstract fun onBindDialogView(view: View)

    /**
     * Called when the dialog has been closed.
     *
     * @param positiveResult Whether the dialog is accepted.
     */
    internal fun doDialogClosed(positiveResult: Boolean) =
            onDialogClosed(positiveResult)

    /**
     * Called when the dialog has been closed.
     *
     * @param positiveResult Whether the dialog is accepted.
     */
    protected abstract fun onDialogClosed(positiveResult: Boolean)
}