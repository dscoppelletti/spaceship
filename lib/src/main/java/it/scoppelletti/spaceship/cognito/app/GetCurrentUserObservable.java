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
import it.scoppelletti.spaceship.cognito.LoginEvent;

/**
 * Retrieves the current user.
 */
final class GetCurrentUserObservable implements
        ObservableOnSubscribe<LoginEvent> {

    /**
     * Sole constructor.
     */
    GetCurrentUserObservable() {
    }

    @Override
    public void subscribe(@NonNull ObservableEmitter<LoginEvent> emitter) throws
            Exception {
        CognitoUser user;
        CognitoUserPool userPool;
        CognitoAdapter adapter;
        GetCurrentUserHandler handler;

        adapter = CognitoAdapter.getInstance();
        userPool = adapter.getUserPool();
        user = userPool.getCurrentUser();
        if (user == null || TextUtils.isEmpty(user.getUserId())) {
            // - AWS SDK for Android 2.4.2
            // Indeed, the returned user is never null: the last logged user is
            // always returned even if the session is expired or canceled
            // invoking the signOut method; if no login ever occurred, the
            // userId property of the returned user is null.
            emitter.onComplete();
            return;
        }

        handler = new GetCurrentUserHandler(user, emitter);
        user.getSession(handler);
    }
}
