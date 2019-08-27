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

package it.scoppelletti.spaceship.os

import android.os.Parcel
import android.os.Parcelable

/**
 * Implements the `Parcelable.Creator` interface.
 *
 * * [Reducing Parcelable boilerplate code using Kotlin](http://medium.com/@BladeCoder/reducing-parcelable-boilerplate-code-using-kotlin-741c3124a49a)
 *
 * @param T      Class of the `Parcelable` objects.
 * @param create Function for creating an instance of class `T`.
 * @since        1.0.0
 */
public inline fun <reified T : Parcelable> parcelableCreator(
        crossinline create: (Parcel) -> T
): Parcelable.Creator<T> =
        object : Parcelable.Creator<T> {

            override fun createFromParcel(source: Parcel): T = create(source)

            override fun newArray(size: Int): Array<out T?> =
                    arrayOfNulls<T>(size)
        }

// In the article
// http://medium.com/@BladeCoder/reducing-parcelable-boilerplate-code-using-kotlin-741c3124a49a
// I can find other extensions for other types.

/**
 * Reads a `Boolean` value from a stream.
 *
 * @receiver Stream.
 * @return   The read value.
 * @since    1.0.0
 */
public fun Parcel.readBoolean(): Boolean = readInt() != 0

/**
 * Writes a `Boolean` value to a stream.
 *
 * @receiver       Stream.
 * @param    value Value.
 * @since          1.0.0
 */
public fun Parcel.writeBoolean(value: Boolean) = writeInt(if (value) 1 else 0)
