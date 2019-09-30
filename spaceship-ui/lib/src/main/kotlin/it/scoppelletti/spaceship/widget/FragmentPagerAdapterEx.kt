/*
 * Copyright (C) 2016 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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
        "RemoveRedundantQualifierName")

package it.scoppelletti.spaceship.widget

import android.util.SparseArray
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * Extended `FragmentPagerAdapter` component.
 *
 * * [ViewPager with FragmentPagerAdapter > Access Fragment Instances](http://guides.codepath.com/android/ViewPager-with-FragmentPagerAdapter#access-fragment-instances)
 *
 * @since 1.0.0
 *
 * @constructor             Constructor.
 * @param       fragmentMgr Fragment manager.
 */
@UiThread
public abstract class FragmentPagerAdapterEx(
        fragmentMgr: FragmentManager
) : FragmentPagerAdapter(fragmentMgr,
        FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val fragments: SparseArray<Fragment> = SparseArray(1)

    /**
     * Gets a fragment by position.
     *
     * @param  position The index.
     * @return          The object. If the fragment has never been instantiated
     *                  or if it has been destroyed, returns {@code null}.
     */
    @Suppress("unused")
    public fun getFragmentByPosition(position: Int): Fragment? =
            fragments[position]

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment: Fragment

        fragment = super.instantiateItem(container, position) as Fragment
        fragments.put(position, fragment)

        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        fragments.remove(position)
        super.destroyItem(container, position, obj)
    }
}
