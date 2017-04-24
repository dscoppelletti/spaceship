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

package it.scoppelletti.spaceship.app;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Notifies the result of a dialog.
 *
 * @since 1.0.0
 */
public final class DialogCloseEvent implements Parcelable {

    /**
     * The {@code Parcelable} support.
     */
    public static final Creator<DialogCloseEvent> CREATOR =
            new Creator<DialogCloseEvent>() {

                @Override
                public DialogCloseEvent createFromParcel(Parcel in) {
                    return new DialogCloseEvent(in);
                }

                @Override
                public DialogCloseEvent[] newArray(int size) {
                    return new DialogCloseEvent[size];
                }
            };

    private final int myReqCode;
    private final Bundle myExtras;
    private int myResult;

    /**
     * Constructor.
     *
     * @param reqCode The request code.
     */
    public DialogCloseEvent(int reqCode) {
        myReqCode = reqCode;
        myExtras = new Bundle(0);
        myResult = DialogInterface.BUTTON_POSITIVE;
    }

    /**
     * Constructor.
     *
     * @param in The parcel.
     */
    private DialogCloseEvent(Parcel in) {
        myReqCode = in.readInt();
        myExtras = in.readBundle(getClass().getClassLoader());
        myResult = in.readInt();
    }

    /**
     * Gets the request code.
     *
     * @return The value.
     */
    public int getRequestCode() {
        return myReqCode;
    }

    /**
     * Gets the map of extended data.
     *
     * @return The collection.
     */
    @NonNull
    public Bundle getExtras() {
        return myExtras;
    }

    /**
     * Gets the button that was clicked or the position of the item clicked when
     * the dialog has been closed.
     *
     * @return The value.
     */
    public int getResult() {
        return myResult;
    }

    /**
     * Sets the button that was clicked or the position of the item clicked when
     * the dialog has been closed.
     *
     * @param value A value.
     */
    public void setResult(int value) {
        myResult = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(myReqCode);
        dest.writeBundle(myExtras);
        dest.writeInt(myResult);
    }
}
