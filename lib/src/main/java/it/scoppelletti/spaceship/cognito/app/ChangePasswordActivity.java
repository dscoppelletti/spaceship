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

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import it.scoppelletti.spaceship.ApplicationException;
import it.scoppelletti.spaceship.ExceptionEvent;
import it.scoppelletti.spaceship.app.AppExt;
import it.scoppelletti.spaceship.app.ExceptionDialogFragment;
import it.scoppelletti.spaceship.cognito.CognitoAdapter;
import it.scoppelletti.spaceship.cognito.R;
import it.scoppelletti.spaceship.cognito.data.ChangePasswordForm;
import it.scoppelletti.spaceship.cognito.data.SpaceshipUser;
import it.scoppelletti.spaceship.cognito.databinding.ChangePasswordActivityBinding;
import it.scoppelletti.spaceship.rx.CompletableCoordinator;
import it.scoppelletti.spaceship.rx.CompleteEvent;
import it.scoppelletti.spaceship.widget.ProgressOverlay;
import it.scoppelletti.spaceship.widget.SnackbarEvent;

/**
 * Activity for changing the password of a user.
 *
 * @since 1.0.0
 */
public final class ChangePasswordActivity extends AppCompatActivity {
    private static final String PROP_FORM = "1";
    private String myUserCode;
    private ProgressOverlay myProgressBar;
    private ChangePasswordActivityBinding myBinding;
    private CompositeDisposable myDisposables;

    /**
     * Sole constructor.
     */
    public ChangePasswordActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toolbar toolbar;
        ChangePasswordForm form;
        SpaceshipUser user;

        super.onCreate(savedInstanceState);
        myBinding = DataBindingUtil.setContentView(this,
                R.layout.it_scoppelletti_cognito_changepassword_activity);
        myDisposables = new CompositeDisposable();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_menu);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            form = new ChangePasswordForm();
        } else {
            form = savedInstanceState.getParcelable(
                    ChangePasswordActivity.PROP_FORM);
        }

        myProgressBar = (ProgressOverlay) findViewById(R.id.progress_bar);

        myBinding.setForm(form);

        user = CognitoAdapter.getInstance().getCurrentUser();
        if (user != null) {
            myBinding.txtName.setText(user.getUserCode());
        }

        myBinding.txtPasswordConfirm.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {

                    @Override
                    public boolean onEditorAction(@NonNull TextView view,
                            int actionId, @Nullable KeyEvent event) {
                        return ChangePasswordActivity.this.onEditorAction(
                                actionId);
                    }
                });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onCancelClick();
            }
        });
    }

    @Override
    protected void onResume() {
        Disposable subscription;
        ChangePasswordActivityData data;
        CompletableCoordinator pwdCoordinator;

        super.onResume();
        EventBus.getDefault().register(this);

        data = AppExt.getOrCreateFragment(this,
                ChangePasswordActivityData.class,
                ChangePasswordActivityData.TAG);
        pwdCoordinator = data.getPasswordCoordinator();
        subscription = pwdCoordinator.subscribe(
                ChangePasswordObserver.newFactory());
        myDisposables.add(subscription);
    }

    @Override
    protected void onPause() {
        myDisposables.dispose();
        myDisposables = new CompositeDisposable();

        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        ChangePasswordForm form;

        super.onSaveInstanceState(outState);

        if (myBinding != null) {
            form = myBinding.getForm();
            if (form != null) {
                outState.putParcelable(ChangePasswordActivity.PROP_FORM, form);
            }
        }
    }

    @Override
    public void onBackPressed() {
        onCancelClick();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater;

        inflater = getMenuInflater();
        inflater.inflate(R.menu.it_scoppelletti_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cmd_ok) {
            onDoneClick();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when an action is being performed.
     *
     * @param  actionId Identifier of the action.
     * @return          Returns {@code true} if the action has been consumed,
     *                  {@code false} otherwise.
     */
    private boolean onEditorAction(int actionId) {
        switch (actionId) {
        case EditorInfo.IME_ACTION_DONE:
            onDoneClick();
            return true;
        }

        return false;
    }

    /**
     * Accepts the activity.
     */
    private void onDoneClick() {
        Disposable connection;
        CompletableCoordinator coordinator;
        ChangePasswordObservable process;
        ChangePasswordForm form;

        AppExt.hideSoftKeyboard(this);

        form = myBinding.getForm();
        if (!form.validate()) {
            return;
        }

        myProgressBar.show();
        try {
            coordinator = AppExt.getOrCreateFragment(this,
                    ChangePasswordActivityData.class,
                    ChangePasswordActivityData.TAG).getPasswordCoordinator();
            if (coordinator.isRunning()) {
                throw new ApplicationException.Builder(
                        R.string.it_scoppelletti_cognito_err_changePasswordAlreadyInProgress)
                        .title(R.string.it_scoppelletti_cmd_changePassword)
                        .build();
            }

            process = new ChangePasswordObservable(
                    CognitoAdapter.getInstance().getCurrentUser().getUser(),
                    form.getPasswordOld(), form.getPasswordNew());
            connection = coordinator.connect(Observable.create(process)
                    .ignoreElements()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()));
            myDisposables.add(connection);
        } catch (RuntimeException ex) {
            EventBus.getDefault().post(new ExceptionEvent(ex)
                    .title(R.string.it_scoppelletti_cmd_changePassword));
        }
    }

    /**
     * Called when the password has been changed.
     *
     * @param event The event.
     */
    @Subscribe
    public void onComplete(CompleteEvent event) {
        myProgressBar.hide(new Runnable() {

            @Override
            public void run() {
                Snackbar.make(myBinding.contentFrame,
                        R.string.it_scoppelletti_msg_changePassword,
                        Snackbar.LENGTH_SHORT)
                        .addCallback(new Snackbar.Callback() {

                            @Override
                            public void onDismissed(Snackbar transientBottomBar,
                                    int event) {
                                ChangePasswordActivity.this.onCancelClick();
                            }
                        }).show();
            }
        });
    }

    /**
     * Cancels the activity.
     */
    private void onCancelClick() {
        if (!isFinishing()) {
            finish();
        }
    }

    /**
     * Shows a message.
     *
     * @param event The event.
     */
    @Subscribe
    public void onSnackbarEvent(@NonNull final SnackbarEvent event) {
        if (event == null) {
            throw new NullPointerException("Argument event is null.");
        }

        myProgressBar.hide(new Runnable() {

            @Override
            public void run() {
                event.show(myBinding.contentFrame);
            }
        });
    }

    /**
     * Handles an exception.
     *
     * @param event The event.
     */
    @Subscribe
    public void onExceptionEvent(@NonNull final ExceptionEvent event) {
        myProgressBar.hide(new Runnable() {

            @Override
            public void run() {
                new ExceptionDialogFragment.Builder(ChangePasswordActivity.this)
                        .exceptionEvent(event)
                        .show();
            }
        });
    }
}
