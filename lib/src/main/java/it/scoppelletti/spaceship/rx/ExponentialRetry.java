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

package it.scoppelletti.spaceship.rx;

import java.util.Locale;
import java.util.concurrent.TimeUnit;
import android.support.annotation.NonNull;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;

/**
 * Handles a specified number of retries using an exponetial back off scheme to
 * determine the interval between retries.
 *
 * <p>Use the {@code ExponentialRetry} class with the {@code retryWhen}
 * method:</p>
 *
 * <blockquote><pre>
 * Completable.fromAction(new Work())
 *     .retryWhen(new ExponentialRetry(3))
 *     .subscribeOn(Schedulers.io())
 *     .observeOn(AndroidSchedulers.mainThread())
 *     .subscribe(new WorkObserver());
 * </pre></blockquote>
 *
 * @since 1.0.0
 */
@Slf4j
public final class ExponentialRetry implements
        Function<Flowable<? extends Throwable>, Publisher<?>> {
    private final int myRetryMax;
    private int myRetryCount;
    private int myRetryDelay;

    /**
     * Constructor.
     *
     * @param retryMax Maximum number of retries.
     */
    public ExponentialRetry(int retryMax) {
        if (retryMax < 0) {
            throw new IllegalArgumentException("Argument retryMax < 0.");
        }

        myRetryMax = retryMax;
        myRetryCount = 0;
        myRetryDelay = 1;
    }

    @NonNull
    @Override
    public Publisher<?> apply(@NonNull Flowable<? extends Throwable>  handler)
            throws Exception {
        return handler.flatMap(new Function<Throwable, Flowable<?>>() {

            @NonNull
            @Override
            public Flowable<?> apply(@NonNull Throwable ex) {
                Flowable<?> ret;

                myRetryCount++;
                if (myRetryCount < myRetryMax) {
                    myLogger.error(String.format(Locale.ENGLISH, "Retry #%1$d.",
                            myRetryCount), ex);
                    ret = Flowable.timer(myRetryDelay, TimeUnit.SECONDS);
                    myRetryDelay *= 2;
                } else {
                    ret = Flowable.error(ex);
                }

                return ret;
            }
        });
    }
}
