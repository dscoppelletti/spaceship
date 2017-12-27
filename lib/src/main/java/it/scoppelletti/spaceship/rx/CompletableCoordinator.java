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
     * @param  task The observable task.
     * @return      The connection.
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
        myConnection = myTask.subscribe(() -> myLogger.debug("Task completed."),
                (ex) -> myLogger.error("Task failed.", ex));
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
     * @param  observerFactory The factory object for creating a new observer
     *                         instance.
     * @return                 The subscription.
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

        subscriptions.add(Disposables.fromRunnable(() -> {
            myLogger.debug("Subscription canceled.");
            myObserverFactory = null;
        }));

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
}
