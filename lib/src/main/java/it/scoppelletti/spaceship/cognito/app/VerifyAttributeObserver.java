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
import io.reactivex.observers.DisposableCompletableObserver;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.EventBus;
import it.scoppelletti.spaceship.ExceptionEvent;
import it.scoppelletti.spaceship.rx.CompleteEvent;

/**
 * Observer for verification attribute process.
 */
@Slf4j
final class VerifyAttributeObserver extends DisposableCompletableObserver {
    private final String myAttr;

    /**
     * Constructor.
     *
     * @param attr Attribute to verify.
     */
    VerifyAttributeObserver(String attr) {
        myAttr = attr;
    }

    @Override
    public void onComplete() {
        myLogger.debug("Verification of attribute {} succeeded.", myAttr);
        EventBus.getDefault().post(CompleteEvent.getInstance());
    }

    @Override
    public void onError(@NonNull Throwable ex) {
        myLogger.error(String.format("Failed to verify attribute %1$s.",
                myAttr), ex);
        EventBus.getDefault().post(new ExceptionEvent(ex)
                .title(VerifyAttributeActivity.getTitleId(myAttr)));
    }
}
