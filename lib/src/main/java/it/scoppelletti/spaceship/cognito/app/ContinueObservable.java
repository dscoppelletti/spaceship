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
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.CognitoIdentityProviderContinuation;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Continues a process.
 */
final class ContinueObservable implements ObservableOnSubscribe<Object> {
    private final CognitoIdentityProviderContinuation<?> myFlow;

    /**
     * Constructor.
     *
     * @param flow State of the flow.
     */
    ContinueObservable(@NonNull CognitoIdentityProviderContinuation<?> flow) {
        if (flow == null) {
            throw new NullPointerException("Argument flow is null.");
        }

        myFlow = flow;
    }

    @Override
    public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws
            Exception {
        ThreadLocalEmitter.getInstance().set(emitter);
        myFlow.continueTask();
    }
}
