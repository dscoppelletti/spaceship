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
import android.support.annotation.StringRes;
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
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
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
import it.scoppelletti.spaceship.app.TitleAdapter;
import it.scoppelletti.spaceship.cognito.CognitoAdapter;
import it.scoppelletti.spaceship.cognito.R;
import it.scoppelletti.spaceship.cognito.data.SpaceshipUser;
import it.scoppelletti.spaceship.cognito.data.UserAttribute;
import it.scoppelletti.spaceship.cognito.data.VerifyAttributeForm;
import it.scoppelletti.spaceship.cognito.databinding.VerifyAttributeActivityBinding;
import it.scoppelletti.spaceship.rx.CompletableCoordinator;
import it.scoppelletti.spaceship.rx.CompleteEvent;
import it.scoppelletti.spaceship.rx.SingleCoordinator;
import it.scoppelletti.spaceship.widget.ProgressOverlay;
import it.scoppelletti.spaceship.widget.SnackbarEvent;

/**
 * Activity to verify an attribute.
 *
 * @since 1.0.0
 */
public final class VerifyAttributeActivity extends AppCompatActivity {
    private static final int REQ_SEND = 1;
    private static final String PROP_FORM = "1";
    private String myAttr;
    private boolean myFirstRun;
    private SpaceshipUser myUser;
    private TitleAdapter myTitleAdapter;
    private ProgressOverlay myProgressBar;
    private VerifyAttributeActivityBinding myBinding;
    private CompositeDisposable myDisposables;

    /**
     * Sole constructor.
     */
    public VerifyAttributeActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toolbar toolbar;
        VerifyAttributeForm form;

        super.onCreate(savedInstanceState);
        myBinding = DataBindingUtil.setContentView(this,
                R.layout.it_scoppelletti_cognito_verifyattribute_activity);
        myDisposables = new CompositeDisposable();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_menu);
        setSupportActionBar(toolbar);

        myTitleAdapter = new TitleAdapter.Builder(this)
            .build();

        myUser = CognitoAdapter.getInstance().getCurrentUser();
        if (myUser == null) {
            throw new NullPointerException("No logged user.");
        }

        myAttr = getIntent().getExtras().getString(
                CognitoAdapter.PROP_ATTRIBUTE);
        if (TextUtils.isEmpty(myAttr)) {
            throw new NullPointerException("Attribute not defined.");
        }

        if (savedInstanceState == null) {
            myFirstRun = true;
            form = new VerifyAttributeForm();
        } else {
            myFirstRun = false;
            form = savedInstanceState.getParcelable(
                    VerifyAttributeActivity.PROP_FORM);
        }

        myProgressBar = (ProgressOverlay) findViewById(R.id.progress_bar);

        myBinding.setForm(form);
        myBinding.txtVerificationCode.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {

                    @Override
                    public boolean onEditorAction(@NonNull TextView view,
                            int actionId, @Nullable KeyEvent event) {
                        return VerifyAttributeActivity.this.onEditorAction(
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
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        myTitleAdapter.onPostCreate(savedInstanceState);

        switch (myAttr) {
        case UserAttribute.ATTR_EMAIL:
            myTitleAdapter.setTitle(
                    R.string.it_scoppelletti_cognito_cmd_verifyEmail);
            myBinding.frmAttribute.setHint(getString(
                    R.string.it_scoppelletti_lbl_email));
            myBinding.txtAttribute.setText(myUser.getEmail());
            break;

        case UserAttribute.ATTR_PHONENUMBER:
            myTitleAdapter.setTitle(
                    R.string.it_scoppelletti_cognito_cmd_verifyPhoneNumber);
            myBinding.frmAttribute.setHint(getString(
                    R.string.it_scoppelletti_lbl_phoneNumber));
            myBinding.txtAttribute.setText(myUser.getPhoneNumber());
            break;
        }
    }

    /**
     * Gets the title.
     *
     * @param  attr Attribute to verify
     * @return      The value as a string resource ID.
     */
    @StringRes
    static int getTitleId(String attr) {
        int titleId;

        switch (attr) {
        case UserAttribute.ATTR_EMAIL:
            titleId = R.string.it_scoppelletti_cognito_cmd_verifyEmail;
            break;

        case UserAttribute.ATTR_PHONENUMBER:
            titleId = R.string.it_scoppelletti_cognito_cmd_verifyPhoneNumber;
            break;

        default:
            titleId = R.string.app_name;
            break;
        }

        return titleId;
    }

    @Override
    protected void onResume() {
        DialogFragment dlg;
        Disposable subscription;
        VerifyAttributeActivityData data;
        CompletableCoordinator verifyCoordinator;
        SingleCoordinator<CognitoUserCodeDeliveryDetails>
                verificationCodeCoordinator;

        super.onResume();
        EventBus.getDefault().register(this);

        data = AppExt.getOrCreateFragment(this,
                VerifyAttributeActivityData.class,
                VerifyAttributeActivityData.TAG);
        verifyCoordinator = data.getVerifyCoordinator();
        subscription = verifyCoordinator.subscribe(
                VerifyAttributeObserver.newFactory(myAttr));
        myDisposables.add(subscription);

        verificationCodeCoordinator = data.getVerificationCodeCoordinator();
        subscription = verificationCodeCoordinator.subscribe(
                VerificationCodeObserver.newFactory(myAttr));
        myDisposables.add(subscription);

        if (myFirstRun) {
            try {
                dlg = VerificationCodeDialogFragment.newInstance(
                        VerifyAttributeActivity.getTitleId(myAttr),
                        VerifyAttributeActivity.REQ_SEND);
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
        VerifyAttributeForm form;

        super.onSaveInstanceState(outState);
        myTitleAdapter.onSaveInstanceState(outState);

        if (myBinding != null) {
            form = myBinding.getForm();
            if (form != null) {
                outState.putParcelable(VerifyAttributeActivity.PROP_FORM, form);
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean enabled;
        MenuItem menuItem;

        switch (myAttr) {
        case UserAttribute.ATTR_EMAIL:
            enabled = (myUser != null &&
                    !TextUtils.isEmpty(myUser.getEmail()) &&
                    !myUser.isEmailVerified());
            break;

        case UserAttribute.ATTR_PHONENUMBER:
            enabled = (myUser != null &&
                    !TextUtils.isEmpty(myUser.getPhoneNumber()) &&
                    !myUser.isPhoneNumberVerified());
            break;

        default:
            enabled = false;
            break;
        }

        menuItem = menu.findItem(R.id.cmd_ok);
        menuItem.setEnabled(enabled);

        return super.onPrepareOptionsMenu(menu);
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
        VerifyAttributeObservable process;
        VerifyAttributeForm form;

        AppExt.hideSoftKeyboard(this);

        form = myBinding.getForm();
        if (!form.validate()) {
            return;
        }

        myProgressBar.show();
        try {
            coordinator = AppExt.getOrCreateFragment(this,
                    VerifyAttributeActivityData.class,
                    VerifyAttributeActivityData.TAG).getVerifyCoordinator();
            if (coordinator.isRunning()) {
                throw new ApplicationException.Builder(
                        R.string.it_scoppelletti_cognito_err_verifyAttributeAlreadyInProgress)
                        .title(VerifyAttributeActivity.getTitleId(myAttr))
                        .build();
            }

            process = new VerifyAttributeObservable(myUser.getUser(), myAttr,
                    form.getVerificationCode());
            connection = coordinator.connect(Observable.create(process)
                    .ignoreElements()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()));
            myDisposables.add(connection);
        } catch (RuntimeException ex) {
            EventBus.getDefault().post(new ExceptionEvent(ex)
                    .title(VerifyAttributeActivity.getTitleId(myAttr)));
        }
    }

    /**
     * Called when the attribute has been verified.
     *
     * @param event The event.
     */
    @Subscribe
    public void onComplete(CompleteEvent event) {
        final int msgId;

        if (myUser == null) {
            throw new NullPointerException("No logged user.");
        }

        switch (myAttr) {
        case UserAttribute.ATTR_EMAIL:
            msgId = R.string.it_scoppelletti_cognito_msg_emailVerified;
            myUser.setEmailVerified(true);
            break;

        case UserAttribute.ATTR_PHONENUMBER:
            msgId = R.string.it_scoppelletti_cognito_msg_phoneNumberVerified;
            myUser.setPhoneNumberVerified(true);
            break;

        default:
            throw new IllegalStateException(String.format(
                    "Attribute %1$s not supported.", myAttr));
        }

        supportInvalidateOptionsMenu();

        myProgressBar.hide(new Runnable() {

            @Override
            public void run() {
                Snackbar.make(myBinding.contentFrame, msgId,
                        Snackbar.LENGTH_SHORT)
                        .addCallback(new Snackbar.Callback() {

                            @Override
                            public void onDismissed(Snackbar transientBottomBar,
                                    int event) {
                                VerifyAttributeActivity.this.onCancelClick();
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
                new ExceptionDialogFragment.Builder(
                        VerifyAttributeActivity.this)
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
        VerificationCodeObservable process;
        SingleCoordinator<CognitoUserCodeDeliveryDetails> coordinator;

        if (event == null) {
            throw new NullPointerException("Argument event is null.");
        }

        if (event.getRequestCode() != VerifyAttributeActivity.REQ_SEND ||
                event.getResult() != DialogInterface.BUTTON_POSITIVE) {
            return;
        }

        myProgressBar.show();
        try {
            coordinator = AppExt.getOrCreateFragment(this,
                    VerifyAttributeActivityData.class,
                    VerifyAttributeActivityData.TAG)
                    .getVerificationCodeCoordinator();
            if (coordinator.isRunning()) {
                throw new ApplicationException.Builder(
                        R.string.it_scoppelletti_cognito_err_sendVerificationCodeAlreadyInProgress)
                        .title(VerifyAttributeActivity.getTitleId(myAttr))
                        .build();
            }

            process = new VerificationCodeObservable(myUser.getUser(), myAttr);
            connection = coordinator.connect(Observable.create(process)
                    .firstOrError()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()));
            myDisposables.add(connection);
        } catch (RuntimeException ex) {
            EventBus.getDefault().post(new ExceptionEvent(ex)
                    .title(VerifyAttributeActivity.getTitleId(myAttr)));
        }
    }
}
