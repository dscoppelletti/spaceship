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

package it.scoppelletti.spaceship.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * Extented `ViewPager` view.
 *
 * * [How do disable paging by swiping with finger in ViewPager but still be able to swipe programmatically?](http://stackoverflow.com/questions/9650265)
 *
 * @since 1.0.0
 *
 * @property swipeEnabled Indicates wheter the user can change page by swiping
 *                        or not.
 *
 * @constructor         Constructor.
 * @param       context Context
 * @param       attrs   Attributes.
 */
@SuppressLint("ClickableViewAccessibility")
public class ViewPagerEx(
        context: Context,
        attrs: AttributeSet
) : ViewPager(context, attrs) {

    public var swipeEnabled: Boolean = true

    override fun onTouchEvent(ev: MotionEvent?): Boolean =
            swipeEnabled && super.onTouchEvent(ev)

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean =
            swipeEnabled && super.onInterceptTouchEvent(ev)
}