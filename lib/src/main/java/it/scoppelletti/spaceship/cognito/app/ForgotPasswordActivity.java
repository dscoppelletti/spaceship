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

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
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
import it.scoppelletti.spaceship.app.DialogCloseEvent;
import it.scoppelletti.spaceship.app.ExceptionDialogFragment;
import it.scoppelletti.spaceship.cognito.CognitoAdapter;
import it.scoppelletti.spaceship.cognito.R;
import it.scoppelletti.spaceship.cognito.data.ForgotPasswordForm;
import it.scoppelletti.spaceship.cognito.databinding.ForgotPasswordActivityBinding;
import it.scoppelletti.spaceship.rx.CompletableCoordinator;
import it.scoppelletti.spaceship.rx.CompleteEvent;
import it.scoppelletti.spaceship.rx.SingleCoordinator;
import it.scoppelletti.spaceship.widget.ProgressOverlay;
import it.scoppelletti.spaceship.widget.SnackbarEvent;

/**
 * Activity for resetting the password of a user.
 *
 * @since 1.0.0
 */
public final class ForgotPasswordActivity extends AppCompatActivity {
    private static final int REQ_SEND = 1;
    private static final String PROP_FORM = "1";
    private String myUserCode;
    private boolean myFirstRun;
    private ProgressOverlay myProgressBar;
    private ForgotPasswordActivityBinding myBinding;
    private CompositeDisposable myDisposables;

    /**
     * Sole constructor.
     */
    public ForgotPasswordActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toolbar toolbar;
        ForgotPasswordForm form;

        super.onCreate(savedInstanceState);
        myBinding = DataBindingUtil.setContentView(this,
                R.layout.it_scoppelletti_cognito_forgotpassword_activity);
        myDisposables = new CompositeDisposable();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_menu);
        setSupportActionBar(toolbar);

        myUserCode = getIntent().getExtras().getString(
                CognitoAdapter.PROP_USERCODE);
        if (TextUtils.isEmpty(myUserCode)) {
            throw new NullPointerException("User code not defined.");
        }

        if (savedInstanceState == null) {
            myFirstRun = true;
            form = new ForgotPasswordForm();
        } else {
            myFirstRun = false;
            form = savedInstanceState.getParcelable(
                    ForgotPasswordActivity.PROP_FORM);
        }

        myProgressBar = (ProgressOverlay) findViewById(R.id.progress_bar);

        myBinding.setForm(form);
        myBinding.txtName.setText(myUserCode);
        myBinding.txtPasswordConfirm.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {

                    @Override
                    public boolean onEditorAction(@NonNull TextView view,
                            int actionId, @Nullable KeyEvent event) {
                        return ForgotPasswordActivity.this.onEditorAction(
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
        DialogFragment dlg;
        Disposable subscription;
        ForgotPasswordActivityData data;
        CompletableCoordinator pwdCoordinator;
        SingleCoordinator<ForgotPasswordContinuation>
                verificationCodeCoordinator;

        super.onResume();
        EventBus.getDefault().register(this);

        data = AppExt.getOrCreateFragment(this,
                ForgotPasswordActivityData.class,
                ForgotPasswordActivityData.TAG);
        pwdCoordinator = data.getPasswordCoordinator();
        subscription = pwdCoordinator.subscribe(
                ConfirmPasswordObserver.newFactory());
        myDisposables.add(subscription);

        verificationCodeCoordinator = data.getVerificationCodeCoordinator();
        subscription = verificationCodeCoordinator.subscribe(
                ForgotPasswordObserver.newFactory());
        myDisposables.add(subscription);

        if (myFirstRun) {
            try {
                dlg = VerificationCodeDialogFragment.newInstance(
                        R.string.it_scoppelletti_cmd_forgotPassword,
                        ForgotPasswordActivity.REQ_SEND);
                dlg.show(getSupportFragmentManager(),
                        VerificationCodeDialogFragment.TAG);
            } finally {
                myFirstRun = false;
            }
        }
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
        ForgotPasswordForm form;

        super.onSaveInstanceState(outState);

        if (myBinding != null) {
            form = myBinding.getForm();
            if (form != null) {
                outState.putParcelable(ForgotPasswordActivity.PROP_FORM, form);
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
        ConfirmPasswordObservable process;
        ForgotPasswordForm form;

        AppExt.hideSoftKeyboard(this);

        form = myBinding.getForm();
        if (!form.validate()) {
            return;
        }

        myProgressBar.show();
        try {
            coordinator = AppExt.getOrCreateFragment(this,
                    ForgotPasswordActivityData.class,
                    ForgotPasswordActivityData.TAG).getPasswordCoordinator();
            if (coordinator.isRunning()) {
                throw new ApplicationException.Builder(
                        R.string.it_scoppelletti_cognito_err_resetPasswordAlreadyInProgress)
                        .title(R.string.it_scoppelletti_cmd_forgotPassword)
                        .build();
            }

            process = new ConfirmPasswordObservable(myUserCode,
                    form.getPasswordNew(), form.getVerificationCode());
            connection = coordinator.connect(Observable.create(process)
                    .ignoreElements()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()));
            myDisposables.add(connection);
        } catch (RuntimeException ex) {
            EventBus.getDefault().post(new ExceptionEvent(ex)
                    .title(R.string.it_scoppelletti_cmd_forgotPassword));
        }
    }

    /**
     * Called when the password has been reset.
     *
     * @param event The event.
     */
    @Subscribe
    public void onComplete(CompleteEvent event) {
        myProgressBar.hide(new Runnable() {

            @Override
            public void run() {
                Snackbar.make(myBinding.contentFrame,
                        R.string.it_scoppelletti_cognito_msg_resetPassword,
                        Snackbar.LENGTH_SHORT)
                        .addCallback(new Snackbar.Callback() {

                            @Override
                            public void onDismissed(Snackbar transientBottomBar,
                                    int event) {
                                ForgotPasswordActivity.this.onCancelClick();
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
                new ExceptionDialogFragment.Builder(ForgotPasswordActivity.this)
                        .exceptionEvent(event)
                        .show();
            }
        });
    }

    /**
     * Handles the result of a dialog.
     *
     * @param event The event.
     */
    @Subscribe
    public void onDialogClose(@NonNull DialogCloseEvent event) {
        Disposable connection;
        ForgotPasswordObservable process;
        SingleCoordinator<ForgotPasswordContinuation> coordinator;

        if (event == null) {
            throw new NullPointerException("Argument event is null.");
        }

        if (event.getRequestCode() != ForgotPasswordActivity.REQ_SEND ||
                event.getResult() != DialogInterface.BUTTON_POSITIVE) {
            return;
        }

        myProgressBar.show();
        try {
            coordinator = AppExt.getOrCreateFragment(this,
                    ForgotPasswordActivityData.class,
                    ForgotPasswordActivityData.TAG)
                    .getVerificationCodeCoordinator();
            if (coordinator.isRunning()) {
                throw new ApplicationException.Builder(
                        R.string.it_scoppelletti_cognito_err_sendVerificationCodeAlreadyInProgress)
                        .title(R.string.it_scoppelletti_cmd_forgotPassword)
                        .build();
            }

            process = new ForgotPasswordObservable(myUserCode);
            connection = coordinator.connect(Observable.create(process)
                    .firstOrError()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()));
            myDisposables.add(connection);
        } catch (RuntimeException ex) {
            EventBus.getDefault().post(new ExceptionEvent(ex)
                    .title(R.string.it_scoppelletti_cmd_forgotPassword));
        }
    }
}
