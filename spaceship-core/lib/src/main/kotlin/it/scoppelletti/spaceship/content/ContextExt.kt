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

package it.scoppelletti.spaceship.content

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat

/**
 * Returns a `Drawable` object associated with a particolar resource ID and
 * styled for the current theme.
 *
 * @receiver    The context.
 * @param    id The resource ID.
 * @returns     The `Drawable` object.
 * @since       1.0.0
 */
public fun Context.getDrawableCompat(@DrawableRes id: Int): Drawable? =
        ContextCompat.getDrawable(this, id)