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

package it.scoppelletti.spaceship.app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import it.scoppelletti.spaceship.ExceptionEvent;
import it.scoppelletti.spaceship.R;
import it.scoppelletti.spaceship.rx.CompletableCoordinator;
import it.scoppelletti.spaceship.rx.CompleteEvent;

/**
 * Base class for a splash activity.
 *
 * @since 1.0.0
 */
@Slf4j
public abstract class SplashActivityBase extends AppCompatActivity {
    private boolean myFirstRun;
    private CompositeDisposable myDisposables;

    /**
     * Sole constructor.
     */
    protected SplashActivityBase() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.it_scoppelletti_splash_activity);
        myDisposables = new CompositeDisposable();

        myFirstRun = (savedInstanceState == null);
    }

    @Override
    protected void onResume() {
        Disposable connection, subscription;
        SplashActivityData data;
        CompletableCoordinator coordinator;

        super.onResume();
        EventBus.getDefault().register(this);

        data = AppExt.getOrCreateFragment(this, SplashActivityData.class,
                SplashActivityData.TAG);
        coordinator = data.getSplashCoordinator();
        subscription = coordinator.subscribe(
                SplashActivityObserver.newFactory());
        myDisposables.add(subscription);

        if (myFirstRun) {
            try {
                connection = coordinator.connect(createTask()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()));
                myDisposables.add(connection);
            } catch (RuntimeException ex) {
                myLogger.error("Splash activity failed.", ex);
                EventBus.getDefault().post(new ExceptionEvent(ex)
                        .requestCode(R.id.it_scoppelletti_splash));
            } finally {
                myFirstRun = false;
            }
        }
    }

    /**
     * Creates the observable task that this activity has to execute.
     *
     * @return The new object.
     */
    @NonNull
    protected abstract Completable createTask();

    @Override
    protected void onPause() {
        myDisposables.dispose();
        myDisposables = new CompositeDisposable();

        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    /**
     * Called when this activity has been completed.
     *
     * @param event The event.
     */
    @Subscribe
    public void onCompleteEvent(@NonNull CompleteEvent event) {
        onSucceeded();
    }

    /**
     * Called when this activity has been succeeded.
     */
    protected abstract void onSucceeded();

    /**
     * Handles an exception.
     *
     * @param event The event.
     */
    @Subscribe
    public void onExceptionEvent(@NonNull ExceptionEvent event) {
        new ExceptionDialogFragment.Builder(this)
                .exceptionEvent(event)
                .show();
    }

    /**
     * Handles the result of a dialog.
     *
     * @param event The event.
     */
    @Subscribe
    public void onDialogCloseEvent(@NonNull DialogCloseEvent event) {
        if (event == null) {
            throw new NullPointerException("Argument event is null.");
        }

        if (event.getRequestCode() == R.id.it_scoppelletti_splash) {
            finish();
        }
    }
}
