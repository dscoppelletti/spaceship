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

@file:Suppress("JoinDeclarationAndAssignment", "RedundantVisibilityModifier",
        "unused")

package it.scoppelletti.spaceship.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import it.scoppelletti.spaceship.R
import kotlin.math.roundToInt

/**
 * Inserts a divider between the items in a `LinearLayoutManager` list. It
 * supports only `VERTICAL` orientations.
 *
 * @since 1.0.0
 *
 * @constructor             Constructor.
 * @param       ctx         Context.
 * @param       marginLeft  Left margin for the divider (px).
 * @param       marginRight Right margin for the divider (px).
 */
public class HorizontalDividerItemDecoration(
        ctx: Context,
        private val marginLeft: Int = 0,
        private val marginRight: Int = 0
) : RecyclerView.ItemDecoration() {

    private val marginHorz: Int
    private val marginVert: Int
    private val divider: Drawable

    init {
        val attrs: TypedArray

        marginHorz = ctx.resources.getDimensionPixelOffset(
                R.dimen.it_scoppelletti_marginHorz)
        marginVert = ctx.resources.getDimensionPixelOffset(
                R.dimen.it_scoppelletti_spacingVert)

        attrs = ctx.obtainStyledAttributes(IntArray(1) {
            android.R.attr.listDivider
        })

        try {
            divider = checkNotNull(attrs.getDrawable(0)) {
                "Divider drawable not found."
            }
        } finally {
            attrs.recycle()
        }
    }

    @Suppress("FoldInitializerAndIfToElvis")
    override fun onDraw(
            c: Canvas,
            parent: RecyclerView,
            state: RecyclerView.State
    ) {
        val left: Int
        val right: Int
        var child: View?

        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            c.clipRect(left, parent.paddingTop, right,
                    parent.height - parent.paddingBottom)
        } else {
            left = 0
            right = parent.width
        }

        val bounds = Rect()
        for (i in 0 until state.itemCount) {
            child = parent.getChildAt(i)
            if (child == null) {
                continue
            }

            val pos = parent.getChildAdapterPosition(child)
            if (pos == state.itemCount - 1) {
                continue
            }

            parent.getDecoratedBoundsWithMargins(child, bounds)
            val bottom = bounds.bottom - child.translationY.roundToInt()
            val top = bottom - divider.intrinsicHeight
            divider.setBounds(left + marginLeft, top, right - marginRight,
                    bottom)
            divider.draw(c)
        }
    }

    override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
    ) {
        val pos = parent.getChildAdapterPosition(view)

        outRect.left = 0
        outRect.right = 0
        outRect.top = if (pos == 0) marginVert else 0
        outRect.bottom = if (pos == state.itemCount - 1) marginVert else
            divider.intrinsicHeight
    }
}
