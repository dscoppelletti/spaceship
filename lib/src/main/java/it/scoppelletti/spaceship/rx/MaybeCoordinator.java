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

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import io.reactivex.Maybe;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableMaybeObserver;
import lombok.extern.slf4j.Slf4j;

/**
 * Helps to coordinate an observable source with the lifecycle of the activity
 * observing it.
 *
 * @param <T> Class of the items to observe.
 * @since     1.0.0
 */
@Slf4j
@MainThread
public final class MaybeCoordinator<T> {
    private Maybe<T> mySource;
    private Disposable myConnection;
    private MaybeObserverFactory<? super T> myObserverFactory;

    /**
     * Sole constructor.
     */
    public MaybeCoordinator() {
    }

    /**
     * Indicates whether the observable source is running.
     *
     * @return Returns {@code true} if the observable source is running,
     *         {@code false} otherwise.
     */
    public boolean isRunning() {
        return (mySource != null);
    }

    /**
     * Starts an observable source.
     *
     * @param  source The observable source.
     * @return        The connection.
     */
    @NonNull
    public Disposable connect(@NonNull Maybe<T> source) {
        CompositeDisposable subscriptions;

        if (source == null) {
            throw new NullPointerException("Argument source is null.");
        }
        if (mySource != null) {
            throw new IllegalStateException("Source already connected.");
        }

        mySource = source.cache();
        myConnection = mySource.subscribe(newOnSuccessObserver(),
                newOnErrorObserver());
        if (myObserverFactory == null) {
            myLogger.debug("Observable source started but not subscribed.");
            return Disposables.empty();
        }

        subscriptions = new CompositeDisposable();
        subscriptions.add(mySource.subscribeWith(myObserverFactory.create()));
        subscriptions.add(mySource.subscribeWith(newOnCompleteObserver()));
        myLogger.debug("Observable source started and subscribed.");

        return subscriptions;
    }

    /**
     * Prepares the subscription to an observable source.
     *
     * @param  observerFactory The factory object for creating a new observer
     *                         instance.
     * @return                 The subscription.
     */
    @NonNull
    public Disposable subscribe(
            @NonNull MaybeObserverFactory<? super T> observerFactory) {
        CompositeDisposable subscriptions;

        if (observerFactory == null) {
            throw new NullPointerException("Argument observerFactory is null.");
        }
        if (myObserverFactory != null) {
            throw new IllegalStateException("Observer factory already set.");
        }

        myObserverFactory = observerFactory;
        subscriptions = new CompositeDisposable();
        if (mySource == null) {
            myLogger.debug("An observer will subscribe the observable source.");
        } else {
            subscriptions.add(mySource.subscribeWith(
                    myObserverFactory.create()));
            subscriptions.add(mySource.subscribeWith(newOnCompleteObserver()));
            myLogger.debug("Observer subscribed.");
        }

        subscriptions.add(newOnDisposeObserver());
        return subscriptions;
    }

    /**
     * Destroys the connection.
     */
    public void onDestroy() {
        if (myConnection != null) {
            myConnection.dispose();
            myConnection = null;
            myLogger.trace("Observerable connection destroyed.");
        }
    }

    /**
     * Creates an observer for the success of the observable source.
     *
     * @return The new object.
     */
    private Consumer<T> newOnSuccessObserver() {
        return new Consumer<T>() {

            @Override
            public void accept(@NonNull T t) throws Exception {
                myLogger.debug("Task completed.");
            }
        };
    }

    /**
     * Creates an observer for the failure of the observable source.
     *
     * @return The new object.
     */
    private Consumer<Throwable> newOnErrorObserver() {
        return new Consumer<Throwable>() {

            @Override
            public void accept(@NonNull Throwable ex) throws Exception {
                myLogger.error("Task failed.", ex);
            }
        };
    }

    /**
     * Creates an observer for the completion (or failure) of the observable
     * source.
     *
     * @return The new object.
     */
    private DisposableMaybeObserver<T> newOnCompleteObserver() {
        return new DisposableMaybeObserver<T>() {

            @Override
            public void onComplete() {
                MaybeCoordinator.this.onComplete();
            }

            @Override
            public void onSuccess(@NonNull T t) {
                MaybeCoordinator.this.onComplete();
            }

            @Override
            public void onError(@NonNull Throwable ex) {
                MaybeCoordinator.this.onComplete();
            }
        };
    }

    /**
     * Notifies the completion (or exception) of the observable source.
     */
    private void onComplete() {
        mySource = null;
        if (myConnection != null) {
            myConnection.dispose();
            myConnection = null;
        }
    }

    /**
     * Create an observer for the canceling of the subscription.
     *
     * @return The new object.
     */
    private Disposable newOnDisposeObserver() {
        return Disposables.fromRunnable(new Runnable() {

            @Override
            public void run() {
                myLogger.debug("Subscription canceled.");
                myObserverFactory = null;
            }
        });
    }
}
