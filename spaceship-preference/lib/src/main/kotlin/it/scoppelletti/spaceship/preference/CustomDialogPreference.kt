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

package it.scoppelletti.spaceship.preference

import android.content.Context
import android.support.annotation.UiThread
import android.support.v7.preference.DialogPreference
import android.util.AttributeSet
import android.view.View

/**
 * Custom setting with an editor dialog [CustomPreferenceDialogFragment].
 *
 * @since 1.0.0
 */
@UiThread
public abstract class CustomDialogPreference : DialogPreference {

    /**
     * @constructor         Constructor.
     * @param       context The context.
     */
    public constructor(context: Context) : super(context)

    /**
     * @constructor         Constructor.
     * @param       context The context.
     * @param       attrs   The attribute set.
     */
    public constructor(
            context: Context,
            attrs: AttributeSet
    ) : super(context, attrs)

    /**
     * @constructor              Constructor.
     * @param       context      The context.
     * @param       attrs        The attribute set.
     * @param       defStyleAttr The default style attributes as a resource ID.
     */
    public constructor(
            context: Context,
            attrs: AttributeSet,
            defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    /**
     * @constructor              Constructor.
     * @param       context      The context.
     * @param       attrs        The attribute set.
     * @param       defStyleAttr The default style attributes as a resource ID.
     * @param       defStyleRes  The default style as a resource ID.
     */
    public constructor(
            context: Context,
            attrs: AttributeSet,
            defStyleAttr: Int,
            defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    internal fun doBindDialogView(view: View) = onBindDialogView(view)

    protected abstract fun onBindDialogView(view: View)

    internal fun doDialogClosed(positiveResult: Boolean) =
            onDialogClosed(positiveResult)

    protected abstract fun onDialogClosed(positiveResult: Boolean)
}