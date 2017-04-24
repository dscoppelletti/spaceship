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
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableSingleObserver;
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
public final class SingleCoordinator<T> {
    private Single<T> mySource;
    private Disposable myConnection;
    private SingleObserverFactory<? super T> myObserverFactory;

    /**
     * Sole constructor.
     */
    public SingleCoordinator() {
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
     * <p>The activity may connect the observable source when you want (for
     * example, when a button has been clicked), but an
     * {@code SingleCoordinator} object can manage only one observable source at
     * a time.<br />
     * If the observable source loads data for the initialization of the
     * activity, the observable source should be connected in the
     * {@code onCreate} method.<br />
     * If an observer is already set, it subscribes to the observable source
     * immediately, otherwise the subscription is postponed to when an observer
     * will be set. The emitted result is not lost because the observable source
     * is converted to an <i>hot</i> observable through by the {@code cache}
     * method.</p>
     *
     * @param  source The observable source.
     * @return        The connection. The activity should collect the returned
     *                connection in order to dispose it in the {@code onPause}
     *                method.
     * @see           #isRunning()
     * @see           #subscribe(SingleObserverFactory)
     * @see           android.app.Activity#onCreate(android.os.Bundle)
     * @see           android.app.Activity#onPause()
     * @see           io.reactivex.Single#cache()
     */
    @NonNull
    public Disposable connect(@NonNull Single<T> source) {
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
     * <p>The activity should be call the {@code subscribe} method in the
     * {@code onResume} method. An {@code ObservableCoordinator} object can
     * manage only one observer at a time.<br />
     * If an observable source is already connected, the observer subscribes
     * to the observable source immediately, otherwhise the subscription is
     * postponed to when an observable source will be connected. If the activity
     * is paused and then resumed, the observable source is not restarted and
     * the emitted result is not lost because the observable source has been
     * converted to an <i>hot</i> observable through by the {@code cache}
     * method.</p>
     *
     * @param  observerFactory The factory object for creating a new observer
     *                         instance.
     * @return                 The subscription. The activity should collect the
     *                         returned subscription in order to dispose it in
     *                         the {@code onPause} method.
     * @see                    #connect(io.reactivex.Single)
     * @see                    android.app.Activity#onResume()
     * @see                    android.app.Activity#onPause()
     * @see                    io.reactivex.Single#cache()
     */
    @NonNull
    public Disposable subscribe(
            @NonNull SingleObserverFactory<? super T> observerFactory) {
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
    private DisposableSingleObserver<T> newOnCompleteObserver() {
        return new DisposableSingleObserver<T>() {

            @Override
            public void onSuccess(@NonNull T t) {
                SingleCoordinator.this.onComplete();
            }

            @Override
            public void onError(@NonNull Throwable ex) {
                SingleCoordinator.this.onComplete();
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
