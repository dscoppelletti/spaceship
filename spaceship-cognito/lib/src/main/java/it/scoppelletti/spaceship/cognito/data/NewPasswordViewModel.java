/*
 * Copyright (C) 2017 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship.cognito.data;

import android.os.Parcel;

/**
 * New password view-model.
 *
 * @since 1.0.0
 */
public final class NewPasswordViewModel extends PasswordViewModel {

    /**
     * The {@code Parcelable} support.
     */
    public static final Creator<NewPasswordViewModel> CREATOR =
            new Creator<NewPasswordViewModel>() {

                @Override
                public NewPasswordViewModel createFromParcel(Parcel in) {
                    NewPasswordViewModel obj;

                    obj = new NewPasswordViewModel();
                    obj.readFromParcel(in);
                    return obj;
                }

                @Override
                public NewPasswordViewModel[] newArray(int size) {
                    return new NewPasswordViewModel[size];
                }
            };

    /**
     * Sole constructor.
     */
    public NewPasswordViewModel() {
        super();
    }
}
