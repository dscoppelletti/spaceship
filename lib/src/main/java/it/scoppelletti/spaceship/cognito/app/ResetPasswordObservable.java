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

package it.scoppelletti.spaceship.cognito.app;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import it.scoppelletti.spaceship.cognito.CognitoAdapter;

/**
 * Reset password process.
 */
final class ResetPasswordObservable implements ObservableOnSubscribe<Object> {
    private final String myUserCode;

    /**
     * Constructor.
     *
     * @param userCode The user code.
     */
    ResetPasswordObservable(@NonNull String userCode) {
        if (TextUtils.isEmpty(userCode)) {
            throw new NullPointerException("Argument userCode is null.");
        }

        myUserCode = userCode;
    }

    @Override
    public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws
            Exception {
        CognitoUser user;
        CognitoUserPool userPool;
        ResetPasswordHandler handler;

        userPool = CognitoAdapter.getInstance().getUserPool();
        user = userPool.getUser(myUserCode);
        ThreadLocalEmitter.getInstance().set(emitter);
        handler = new ResetPasswordHandler(myUserCode);
        user.forgotPassword(handler);
    }
}
