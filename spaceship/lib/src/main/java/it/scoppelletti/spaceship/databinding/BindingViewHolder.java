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

package it.scoppelletti.spaceship.databinding;

import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;

/**
 * Helper object for implementing the <i>ViewHolder</i> pattern with
 * {@code RecyclerView} views.
 *
 * @since 1.0.0
 */
@UiThread
public final class BindingViewHolder<T extends ViewDataBinding> extends
        RecyclerView.ViewHolder {
    private final T myBinding;

    /**
     * Constructor.
     *
     * @param binding The binding object.
     */
    public BindingViewHolder(@NonNull T binding) {
        super(binding.getRoot());
        myBinding = binding;
    }

    /**
     * Gets the binding object.
     *
     * @return The object.
     */
    @NonNull
    public T getBinding() {
        return myBinding;
    }
}
