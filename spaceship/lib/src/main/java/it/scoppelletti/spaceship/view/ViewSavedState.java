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

package it.scoppelletti.spaceship.view;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

/**
 * Component state.
 *
 * @since 1.0.0
 */
public final class ViewSavedState extends View.BaseSavedState {
    private Bundle myData;

    /**
     * The {@code Parcelable} support.
     */
    public static final Parcelable.Creator<ViewSavedState> CREATOR =
            new Parcelable.Creator<ViewSavedState>() {

                @Override
                public ViewSavedState createFromParcel(Parcel in) {
                    return new ViewSavedState(in);
                }

                @Override
                public ViewSavedState[] newArray(int size) {
                    return new ViewSavedState[size];
                }
            };

    /**
     * Constructor.
     *
     * @param source The input stream.
     */
    public ViewSavedState(Parcelable source) {
        super(source);
    }

    /**
     * Constructor.
     *
     * @param source The input stream.
     */
    private ViewSavedState(Parcel source) {
        super(source);

        myData = source.readBundle(getClass().getClassLoader());
    }

    /**
     * Gets the state data.
     *
     * @return The object.
     */
    public Bundle getData() {
        return myData;
    }

    /**
     * Set the state data.
     *
     * @param obj The object.
     */
    public void setData(Bundle obj) {
        myData = obj;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeBundle(myData);
    }
}
