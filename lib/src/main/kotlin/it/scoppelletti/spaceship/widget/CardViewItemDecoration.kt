/*
 * Copyright (C) 2015 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

@file:Suppress("JoinDeclarationAndAssignment", "RedundantVisibilityModifier")

package it.scoppelletti.spaceship.widget

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.scoppelletti.spaceship.R

/**
 * Inserts the margins around the `CardView` items in a `LinearLayoutManager`
 * list. It supports both `HORIZONTAL` and `VERTICAL` orientations.
 *
 * @since 1.0.0
 *
 * @property orientation Can be either `LinearLayoutManager.HORIZONTAL` or
 *                       `LinearLayoutManager.VERTICAL`.
 *
 * @constructor     Constructor.
 * @param       ctx Context.
 */
@Suppress("unused")
public class CardViewItemDecoration(
        ctx: Context,
        private val orientation: Int
) : RecyclerView.ItemDecoration() {

    private val marginHorz: Int
    private val marginVert: Int

    init {
        marginHorz = ctx.resources.getDimensionPixelOffset(
                R.dimen.it_scoppelletti_marginHorz)
        marginVert = ctx.resources.getDimensionPixelOffset(
                R.dimen.it_scoppelletti_spacingVert)
    }

    override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
    ) {
        outRect.left = marginHorz
        outRect.bottom = marginVert

        when (orientation) {
            LinearLayoutManager.HORIZONTAL -> {
                val pos = parent.getChildAdapterPosition(view)
                outRect.right = if (pos == 0) marginHorz else 0
                outRect.top = marginVert
            }

            LinearLayoutManager.VERTICAL -> {
                val pos = parent.getChildAdapterPosition(view)
                outRect.right = marginHorz
                outRect.top = if (pos == 0) marginVert else 0
            }

            else -> {
                outRect.right = marginHorz
                outRect.top = marginVert
            }
        }
    }
}

