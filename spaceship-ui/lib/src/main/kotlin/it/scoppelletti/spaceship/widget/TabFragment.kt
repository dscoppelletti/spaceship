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

@file:Suppress("RedundantVisibilityModifier", "unused")

package it.scoppelletti.spaceship.widget

import android.os.Bundle
import androidx.viewpager.widget.ViewPager

/**
 * Marks a fragment as managed by a widget `ViewPager` or `ViewPager2`.
 *
 * The widgets `ViewPager` and `ViewPager2` retain at least one page to either
 * side of the current page in the view hierarchy in an idle state, but that may
 * consume too much resources; furthermore if more than one page observe the
 * same source, that may cause conflicts.
 *
 * * [ViewPager.setOffscreenPageLimit](http://developer.android.com/reference/androidx/viewpager/widget/ViewPager.html#setOffscreenPageLimit(int))
 *
 * @since 1.0.0
 */
interface TabFragment {

    /**
     * Called when the fragment is selected.
     *
     * Here you should create the most of resources and subscribe observables.
     *
     * @param savedInstanceState If the parent fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    fun onSelect(savedInstanceState: Bundle?)

    /**
     * Called when another fragment is selected.
     *
     * Here you should release the resources and unsubscribe the observables
     * that the method [onSelect] has created and subscribed.
     */
    fun onDeselect()

    /**
     * Listener to be added to the `ViewPager` widget.
     *
     * ```
     * override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
     *     super.onViewCreated(view, savedInstanceState)
     *
     *     ...
     *
     *     adapter = ...
     *     onPageChangeListener = TabFragment.OnTabChangeListener(adapter)
     *     viewPager.adapter = adapter
     *     viewPager.addOnPageChangeListener(onPageChangeListener)
     * }
     * ```
     *
     * @since 1.0.0
     */
    class OnTabChangeListener(
            private val adapter: FragmentPagerAdapterEx
    ) : ViewPager.OnPageChangeListener {

        /**
         * Invokes the method `onPageSelected` of the interface [TabFragment].
         *
         * You should call this method *after* the end of the method
         * `onActivityCreate` of the fragment hosting the `ViewPager` widget.
         *
         * ```
         * override fun onActivityCreated(savedInstanceState: Bundle?) {
         *     super.onActivityCreated(savedInstanceState)
         *
         *     ...
         *
         *     view.post {
         *         onPageChangeListener.onActivityCreated(savedInstanceState, viewPager.currentItem)
         *     }
         * }
         * ```
         *
         * @param savedInstanceState If the parent fragment is being re-created
         *                           from a previous saved state, this is the
         *                           state.
         * @param position           Position of the initially selected child
         *                           fragment.
         */
        fun onActivityCreated(savedInstanceState: Bundle?, position: Int) {
            doPageSelected(savedInstanceState, position)
        }

        override fun onPageSelected(position: Int) {
            doPageSelected(null, position)
        }

        private fun doPageSelected(savedInstanceState: Bundle?, position: Int) {
            for (i in 0 until adapter.count) {
                (adapter.getFragmentByPosition(i) as? TabFragment)?.onDeselect()
            }

            (adapter.getFragmentByPosition(position) as? TabFragment)
                    ?.onSelect(savedInstanceState)
        }

        override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
        ) {
        }

        override fun onPageScrollStateChanged(state: Int) {
        }
    }
}
