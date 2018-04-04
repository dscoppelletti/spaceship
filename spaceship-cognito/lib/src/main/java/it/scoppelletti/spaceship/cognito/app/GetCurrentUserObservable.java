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
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import io.reactivex.Emitter;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import lombok.extern.slf4j.Slf4j;
import it.scoppelletti.spaceship.cognito.CognitoAdapter;

/**
 * Retrieves the current user.
 */
@Slf4j
final class GetCurrentUserObservable implements
        ObservableOnSubscribe<CognitoUser>, AuthenticationHandler {
    private CognitoUser myUser;
    private Emitter<CognitoUser> myEmitter;

    /**
     * Sole constructor.
     */
    GetCurrentUserObservable() {
    }

    @Override
    public void subscribe(@NonNull ObservableEmitter<CognitoUser> emitter)
            throws Exception {
        CognitoUser user;
        CognitoUserPool userPool;
        CognitoAdapter adapter;

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

        myUser = user;
        myEmitter = emitter;
        user.getSession(this);
    }

    @Override
    public void onSuccess(CognitoUserSession userSession,
            CognitoDevice newDevice) {
        myLogger.debug("User {} is still logged.", myUser.getUserId());
        myEmitter.onNext(myUser);
        myEmitter.onComplete();
    }

    @Override
    public void onFailure(Exception ex) {
        myLogger.error(String.format("User %1$s is not logged anymore.",
                myUser.getUserId()), ex);
        myEmitter.onComplete();
    }

    @Override
    public void getAuthenticationDetails(AuthenticationContinuation flow,
            String userId) {
        myLogger.debug("User {} is not logged anymore.", myUser.getUserId());
        myEmitter.onComplete();
    }

    @Override
    public void getMFACode(MultiFactorAuthenticationContinuation flow) {
        myLogger.error("MFA not supported.");
        myEmitter.onComplete();
    }

    @Override
    public void authenticationChallenge(ChallengeContinuation flow) {
        myLogger.debug("Will not respond to the challange {}.",
                flow.getChallengeName());
        myEmitter.onComplete();
    }
}
