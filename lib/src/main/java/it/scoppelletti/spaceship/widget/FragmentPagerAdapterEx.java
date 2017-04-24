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

package it.scoppelletti.spaceship.widget;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

/**
 * Extended {@code FragmentPagerAdapter}.
 *
 * @see   <a href="http://guides.codepath.com/android/ViewPager-with-FragmentPagerAdapter#access-fragment-instances"
 *        target="_top">ViewPager with FragmentPagerAdapter &gt; Access Fragment
 *        Instances</a>
 * @since 1.0.0
 */
@UiThread
public abstract class FragmentPagerAdapterEx extends FragmentPagerAdapter {
    private final SparseArray<Fragment> myFragments;

    /**
     * Constructor.
     *
     * @param fragmentMgr Fragment manager.
     */
    protected FragmentPagerAdapterEx(@NonNull FragmentManager fragmentMgr) {
        super(fragmentMgr);

        myFragments = new SparseArray<>(1);
    }

    /**
     * Gets a fragment by position.
     *
     * @param  position The index.
     * @return          The object. If the fragment has never been instantiated
     *                  or if it has been destroyed, returns {@code null}.
     */
    @Nullable
    public Fragment getFragmentByPosition(int position) {
        return myFragments.get(position);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment fragment;

        fragment = (Fragment) super.instantiateItem(container, position);
        myFragments.put(position, fragment);

        return fragment;
    }

    @Override
    public void destroyItem(@Nullable ViewGroup container, int position,
            Object object) {
        myFragments.remove(position);
        super.destroyItem(container, position, object);
    }
}
