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

package it.scoppelletti.spaceship.text

import android.text.Spannable

/**
 * Implementation of `Spannable.Factory` interface.
 *
 * When you set a text to a `TextView` widget, by default, the widget creates a
 * copy of the `CharSequence` object and holds it in memory.
 *
 * If you want to reuse the same `TextView` widget to set the text multiple
 * times (such as when using a `RecyclerView.ViewHolder` view), you can set the
 * `ShallowSpannableFactory` object so that the `TextView` widget uses the
 * original `Spannable` object instead of creating a new text object.
 *
 * The `ShallowSpannableFactory` object requires that you use the method
 * `setText(spannableObject, BufferType.SPANNABLE)` when you set the text to the
 * `TextView` widget, otherwise the operation throws a `ClassCastException`
 * exception.
 *
 * * [Spans](http://developer.android.com/guide/topics/text/spans)
 */
public object ShallowSpannableFactory : Spannable.Factory() {

    override fun newSpannable(source: CharSequence?): Spannable {
        return source as Spannable
    }
}

