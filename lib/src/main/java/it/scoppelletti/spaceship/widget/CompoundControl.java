/*
 * Copyright (C) 2013-2017 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.FrameLayout;
import it.scoppelletti.spaceship.view.ViewSavedState;

/**
 * Base class for compound controls.
 *
 * @since 1.0.0
 */
public abstract class CompoundControl extends FrameLayout {

    /**
     * Costruttore.
     *
     * @param ctx The context.
     */
    protected CompoundControl(@NonNull Context ctx) {
        super(ctx);
    }

    /**
     * Constructor.
     *
     * @param ctx   The context.
     * @param attrs The attributes. May be {@code null}.
     */
    protected CompoundControl(@NonNull Context ctx,
            @Nullable AttributeSet attrs) {
        super(ctx, attrs);
    }

    @Override
    protected final void dispatchSaveInstanceState(
            SparseArray<Parcelable> container) {
        // Save the state for the container only; disable the saving for the
        // contained controls.
        super.dispatchFreezeSelfOnly(container);
    }

    @Override
    protected final void dispatchRestoreInstanceState(
            SparseArray<Parcelable> container) {
        // Restore the state for the container only; disable the restoring for
        // the contained controls.
        super.dispatchThawSelfOnly(container);
    }

    @Override
    protected final void onRestoreInstanceState(@NonNull Parcelable state) {
        Bundle data;
        ViewSavedState savedState;

        if (!(state instanceof ViewSavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        savedState = (ViewSavedState) state;

        super.onRestoreInstanceState(savedState.getSuperState());
        data = savedState.getData();

        if (data != null) {
            onRestoreInstanceState(data);
        }
    }

    /**
     * Restores the instance state.
     *
     * @param state The instance state.
     */
    protected abstract void onRestoreInstanceState(@NonNull Bundle state);

    @Override
    protected final Parcelable onSaveInstanceState() {
        Bundle data;
        Parcelable source;
        ViewSavedState state;

        data = new Bundle();
        onSaveInstanceState(data);

        source = super.onSaveInstanceState();
        state = new ViewSavedState(source);
        state.setData(data);

        return state;
    }

    /**
     * Saves the instance state.
     *
     * @param state The instance state.
     */
    protected abstract void onSaveInstanceState(@NonNull Bundle state);
}