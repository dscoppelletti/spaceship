/*
 * Copyright (C) 2013-2019 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship.graphics

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import it.scoppelletti.spaceship.content.res.ResourcesExt

/**
 * Converts a Vector Drawable in a Bitmap.
 *
 * @receiver
 * @param    drawableId Vector Drawable to convert as a resource ID.
 * @param    tintColor  Tint to apply as a resource ID.
 * @return              Corresponding Bitmap.
 * @since               1.0.0
 *
 * * [Using Vector Drawables as Google Map Markers on Android](http://proandroiddev.com/using-vector-drawables-as-google-map-markers-on-android-1eb69790fc61)
 * * [Vector Drawables compatibility](http://github.com/dscoppelletti/spaceship/wiki/Vector-Drawables-compatibility)
 */
public fun Context.vectorDrawableToBitmap(
        @DrawableRes drawableId: Int,
        @ColorRes tintColor: Int = ResourcesExt.ID_NULL
) : Bitmap {
    val drawable = checkNotNull(ContextCompat.getDrawable(this, drawableId)) {
        "Drawable $drawableId not found."
    }.also {
        it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)
        if (tintColor != ResourcesExt.ID_NULL) {
            DrawableCompat.setTint(it, ContextCompat.getColor(this, tintColor))
        }
    }

    return Bitmap.createBitmap(drawable.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888).also {
        val canvas = Canvas(it)
        drawable.draw(canvas)
    }
}
