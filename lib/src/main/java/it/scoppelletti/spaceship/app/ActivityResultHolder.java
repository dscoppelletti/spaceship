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

package it.scoppelletti.spaceship.app;

import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Holds the incoming result from an activity started with the
 * {@code startActivityForResult} method for further processing.
 *
 * @see   android.app.Activity#onActivityResult(int, int, android.content.Intent)
 * @see   android.app.Activity#startActivityForResult(android.content.Intent, int)
 * @see   <a href="http://stackoverflow.com/questions/4253118"
 *        target="_blank">Is onResume() called before onActivityResult()?</a>
 * @since 1.0.0
 */
public final class ActivityResultHolder {
    private final int myReqCode;
    private final int myResCode;
    private final Intent myData;

    /**
     * Constructor.
     *
     * @param requestCode The request code.
     * @param resultCode  The result code.
     * @param data        An intent that can provide result data. May be
     *                    {@code null}.
     */
    public ActivityResultHolder(int requestCode, int resultCode,
            @Nullable Intent data) {
        myReqCode = requestCode;
        myResCode = resultCode;
        myData = data;
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
     * Gets the result code.
     *
     * @return The value.
     */
    public int getResultCode() {
        return myResCode;
    }

    /**
     * Gets the intent that can provide result data.
     *
     * @return The object. May be {@code null}.
     */
    @Nullable
    public Intent getData() {
        return myData;
    }
}
