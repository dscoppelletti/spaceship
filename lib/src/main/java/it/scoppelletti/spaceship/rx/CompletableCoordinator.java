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
import io.reactivex.Completable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableCompletableObserver;
import lombok.extern.slf4j.Slf4j;

/**
 * Helps to coordinate an observable task with the lifecycle of the activity
 * observing it.
 *
 * @since 1.0.0
 */
@Slf4j
@MainThread
public final class CompletableCoordinator {
    private Completable myTask;
    private Disposable myConnection;
    private CompletableObserverFactory myObserverFactory;

    /**
     * Sole constructor.
     */
    public CompletableCoordinator() {
    }

    /**
     * Indicates whether the observable task is running.
     *
     * @return Returns {@code true} if the observable task is running,
     *         {@code false} otherwise.
     */
    public boolean isRunning() {
        return (myTask != null);
    }

    /**
     * Starts an observable task.
     *
     * <p>The activity may connect the observable task when you want (for
     * example, when a button has been clicked), but a
     * {@code CompletableCoordinator} object can manage only one observable task
     * at a time.<br />
     * If an observer is already set, it subscribes to the observable task
     * immediately, otherwise the subscription is postponed to when an observer
     * will be set.</p>
     *
     * @param  task The observable task.
     * @return      The connection. The activity should collect the returned
     *              connection in order to dispose it in the {@code onPause}
     *              method.
     * @see         #isRunning()
     * @see         #subscribe(CompletableObserverFactory)
     * @see         android.app.Activity#onPause()
     */
    @NonNull
    public Disposable connect(@NonNull Completable task) {
        CompositeDisposable subscriptions;

        if (task == null) {
            throw new NullPointerException("Argument task is null.");
        }
        if (myTask != null) {
            throw new IllegalStateException("Task already connected.");
        }

        myTask = task.cache();
        myConnection = myTask.subscribe(newOnSuccessObserver(),
                newOnErrorObserver());
        if (myObserverFactory == null) {
            myLogger.debug("Task started but not subscribed.");
            return Disposables.empty();
        }

        subscriptions = new CompositeDisposable();
        subscriptions.add(myTask.subscribeWith(myObserverFactory.create()));
        subscriptions.add(myTask.subscribeWith(newOnCompleteObserver()));
        myLogger.debug("Task started and subscribed.");

        return subscriptions;
    }

    /**
     * Prepares the subscription to an observable task.
     *
     * <p>The activity should be call the {@code subscribe} method in the
     * {@code onResume} method. A {@code CompletableCoordinator} object can
     * manage only one observer at a time.<br />
     * If an observable task is already connected, the observer subscribes
     * to the observable task immediately, otherwhise the subscription is
     * postponed to when an observable task will be connected.</p>
     *
     * @param  observerFactory The factory object for creating a new observer
     *                         instance.
     * @return                 The subscription. The activity should collect the
     *                         returned subscription in order to dispose it in
     *                         the {@code onPause} method.
     * @see                    #connect(io.reactivex.Completable)
     * @see                    android.app.Activity#onResume()
     * @see                    android.app.Activity#onPause()
     */
    @NonNull
    public Disposable subscribe(
            @NonNull CompletableObserverFactory observerFactory) {
        CompositeDisposable subscriptions;

        if (observerFactory == null) {
            throw new NullPointerException("Argument observerFactory is null.");
        }
        if (myObserverFactory != null) {
            throw new IllegalStateException("Observer factory already set.");
        }

        myObserverFactory = observerFactory;
        subscriptions = new CompositeDisposable();
        if (myTask == null) {
            myLogger.debug("An observer will subscribe the task.");
        } else {
            subscriptions.add(myTask.subscribeWith(myObserverFactory.create()));
            subscriptions.add(myTask.subscribeWith(newOnCompleteObserver()));
            myLogger.debug("Observer subscribed.");
        }

        subscriptions.add(newOnDisposeObserver());
        return subscriptions;
    }

    /**
     * Destroy the connection.
     */
    public void onDestroy() {
        if (myConnection != null) {
            myConnection.dispose();
            myConnection = null;
            myLogger.debug("Task connection destroyed.");
        }
    }

    /**
     * Creates an observer for the success of the observable task.
     *
     * @return The new object.
     */
    private Action newOnSuccessObserver() {
        return new Action() {

            @Override
            public void run() throws Exception {
                myLogger.debug("Task completed.");
            }
        };
    }

    /**
     * Creates an observer for the failure of the observable task.
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
     * task.
     *
     * @return The new object.
     */
    private DisposableCompletableObserver newOnCompleteObserver() {
        return new DisposableCompletableObserver() {

            @Override
            public void onComplete() {
                CompletableCoordinator.this.onComplete();
            }

            @Override
            public void onError(@NonNull Throwable ex) {
                CompletableCoordinator.this.onComplete();
            }
        };
    }

    /**
     * Notifies the completion (or exception) of the observable task.
     */
    private void onComplete() {
        myTask = null;
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
