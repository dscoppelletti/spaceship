/*
 * Copyright (C) 2020 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship.widget

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.UiThread
import androidx.core.content.res.getColorOrThrow
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import it.scoppelletti.spaceship.R

/**
 * Extended `SwipeRefreshLayout` component.
 *
 * @since 1.0.0
 *
 * @constructor
 * @param       ctx   Context.
 * @param       attrs Attributes.
 */
@UiThread
public class SwipeRefreshLayoutEx @JvmOverloads constructor(
        ctx: Context,
        attrs: AttributeSet? = null
) : SwipeRefreshLayout(ctx, attrs) {

    init {
        val color: Int
        val v = ctx.obtainStyledAttributes(intArrayOf(R.attr.colorSecondary))

        color = try {
            v.getColorOrThrow(0)
        } finally {
            v.recycle()
        }

        setColorSchemeColors(color)
    }
}
