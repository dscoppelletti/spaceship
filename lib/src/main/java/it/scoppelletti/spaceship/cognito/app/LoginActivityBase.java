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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.NewPasswordContinuation;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import it.scoppelletti.spaceship.ApplicationException;
import it.scoppelletti.spaceship.ExceptionEvent;
import it.scoppelletti.spaceship.app.ActivityResultHolder;
import it.scoppelletti.spaceship.app.AppExt;
import it.scoppelletti.spaceship.app.ExceptionDialogFragment;
import it.scoppelletti.spaceship.cognito.CognitoAdapter;
import it.scoppelletti.spaceship.cognito.LoginEvent;
import it.scoppelletti.spaceship.cognito.NewPasswordEvent;
import it.scoppelletti.spaceship.cognito.R;
import it.scoppelletti.spaceship.cognito.ResetPasswordEvent;
import it.scoppelletti.spaceship.cognito.UserAttribute;
import it.scoppelletti.spaceship.cognito.data.LoginForm;
import it.scoppelletti.spaceship.cognito.data.UserAttributeForm;
import it.scoppelletti.spaceship.cognito.databinding.LoginActivityBinding;
import it.scoppelletti.spaceship.rx.CompleteEvent;
import it.scoppelletti.spaceship.rx.SingleCoordinator;
import it.scoppelletti.spaceship.security.SecureString;
import it.scoppelletti.spaceship.widget.ProgressOverlay;
import it.scoppelletti.spaceship.widget.SnackbarEvent;

/**
 * Base class for a login activity.
 *
 * @since 1.0.0
 */
@Slf4j
public abstract class LoginActivityBase extends AppCompatActivity {
    private static final int REQ_NEWPASSWORD = 1;
    private static final int REQ_RESETPASSWORD = 2;
    private static final String PROP_FORM = "1";
    private boolean myFirstRun;
    private ProgressOverlay myProgressBar;
    private LoginActivityBinding myBinding;
    private NewPasswordContinuation myNewPwdFlow;
    private ForgotPasswordContinuation myResetPwdFlow;
    private ActivityResultHolder myActivityResult;
    private CompositeDisposable myDisposables;

    /**
     * Sole constructor.
     */
    protected LoginActivityBase() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button cmd;
        Toolbar toolbar;
        LoginForm form;

        super.onCreate(savedInstanceState);
        myBinding = DataBindingUtil.setContentView(this,
                R.layout.it_scoppelletti_cognito_login_activity);
        myDisposables = new CompositeDisposable();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myProgressBar = (ProgressOverlay) findViewById(R.id.progress_bar);

        if (savedInstanceState == null) {
            myFirstRun = true;
            form = new LoginForm();
        } else {
            myFirstRun = false;
            form = savedInstanceState.getParcelable(
                    LoginActivityBase.PROP_FORM);
        }

        myBinding.setForm(form);
        myBinding.txtPassword.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {

                    @Override
                    public boolean onEditorAction(@NonNull TextView view,
                            int actionId, @Nullable KeyEvent event) {
                        return LoginActivityBase.this.onEditorAction(actionId);
                    }
                });

        cmd = (Button) findViewById(R.id.cmd_login);
        cmd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onLoginClick();
            }
        });

        cmd = (Button) findViewById(R.id.cmd_resetPassword);
        cmd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onResetPasswordClick();
            }
        });
    }

    @Override
    protected void onResume() {
        Disposable subscription;
        LoginActivityData data;
        SingleCoordinator<Object> loginCoordinator, resetPwdCoordinator;
        SingleCoordinator<LoginEvent> currentUserCoordinator;

        super.onResume();
        EventBus.getDefault().register(this);

        data = AppExt.getOrCreateFragment(this, LoginActivityData.class,
                CognitoAdapter.TAG_LOGINDATA);
        loginCoordinator = data.getLoginCoordinator();
        subscription = loginCoordinator.subscribe(LoginObserver.newFactory());
        myDisposables.add(subscription);

        currentUserCoordinator = data.getCurrentUserCoordinator();
        subscription = currentUserCoordinator.subscribe(
                GetCurrentUserObserver.newFactory());
        myDisposables.add(subscription);

        resetPwdCoordinator = data.getResetPasswordCoordinator();
        subscription = resetPwdCoordinator.subscribe(
                ResetPasswordObserver.newFactory());
        myDisposables.add(subscription);

        if (myActivityResult != null) {
            try {
                onActivityResult();
            } finally {
                myActivityResult = null;
            }

            // better
            return;
        }

        if (myFirstRun) {
            try {
                getCurrentUser(currentUserCoordinator);
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
        LoginForm form;

        super.onSaveInstanceState(outState);

        if (myBinding != null) {
            form = myBinding.getForm();
            if (form != null) {
                outState.putParcelable(LoginActivityBase.PROP_FORM, form);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        myActivityResult = new ActivityResultHolder(requestCode, resultCode,
                data);
    }

    /**
     * Handles the incoming result from an activity started with the
     * {@code startActivityForResult} method.
     */
    private void onActivityResult() {
        switch (myActivityResult.getRequestCode()) {
        case LoginActivityBase.REQ_NEWPASSWORD:
            onNewPasswordResult(myActivityResult.getResultCode(),
                    myActivityResult.getData());
            break;

        case LoginActivityBase.REQ_RESETPASSWORD:
            onResetPasswordResult(myActivityResult.getResultCode(),
                    myActivityResult.getData());
            break;
        }
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
            onLoginClick();
            return true;
        }

        return false;
    }

    /**
     * Called when a process is completed.
     *
     * @param event The event.
     */
    @Subscribe
    public void onCompleteEvent(@NonNull CompleteEvent event) {
        myProgressBar.hide();
    }

    /**
     * Shows a message.
     *
     * @param event The event.
     */
    @Subscribe
    public void onSnackbarEvent(final @NonNull SnackbarEvent event) {
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
    public void onExceptionEvent(final @NonNull ExceptionEvent event) {
        myProgressBar.hide(new Runnable() {

            @Override
            public void run() {
                new ExceptionDialogFragment.Builder(LoginActivityBase.this)
                        .exceptionEvent(event)
                        .show();
            }
        });
    }

    /**
     * Retrieves the current user.
     *
     * @param coordinator Coordinator.
     */
    private void getCurrentUser(SingleCoordinator<LoginEvent> coordinator) {
        Disposable connection;
        GetCurrentUserObservable process;

        myProgressBar.show();
        try {
            process = new GetCurrentUserObservable();
            connection = coordinator.connect(Observable.create(process)
                    .firstOrError()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()));
            myDisposables.add(connection);
        } catch (RuntimeException ex) {
            myProgressBar.hide();
            myLogger.error("Failed to get the current user.", ex);
        }
    }

    /**
     * Performs the login process.
     */
    private void onLoginClick() {
        LoginForm form;
        Disposable connection;
        LoginObservable process;
        SingleCoordinator<Object> coordinator;

        AppExt.hideSoftKeyboard(this);

        form = myBinding.getForm();
        if (!form.validateLogin()) {
            return;
        }

        myProgressBar.show();
        try {
            coordinator = AppExt.getOrCreateFragment(this,
                    LoginActivityData.class, CognitoAdapter.TAG_LOGINDATA)
                    .getLoginCoordinator();
            if (coordinator.isRunning()) {
                throw new ApplicationException.Builder(
                        R.string.it_scoppelletti_cognito_err_loginAlreadyInProgress)
                        .title(R.string.it_scoppelletti_cmd_login).build();
            }

            process = new LoginObservable(form.getUserCode(),
                    new SecureString(form.getPassword()));
            connection = coordinator.connect(Observable.create(process)
                    .firstOrError()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()));
            myDisposables.add(connection);
        } catch (RuntimeException ex) {
            EventBus.getDefault().post(new ExceptionEvent(ex)
                    .title(R.string.it_scoppelletti_cmd_login));
        }
    }

    /**
     * Called when the login has been succeeded.
     *
     * @param event The event.
     */
    @Subscribe
    public void onLoginEvent(@NonNull LoginEvent event) {
        myProgressBar.hide(new Runnable() {

            @Override
            public void run() {
                onLoginSucceeded();
            }
        });
    }

    /**
     * Called when the login has been succeeded.
     */
    protected abstract void onLoginSucceeded();

    /**
     * Prompts the user for a new password.
     *
     * @param event The event.
     */
    @Subscribe
    public void onNewPasswordPrompt(final @NonNull NewPasswordEvent event) {
        if (event == null) {
            throw new NullPointerException("Argument event is null.");
        }

        myProgressBar.hide(new Runnable() {

            @Override
            public void run() {
                Intent intent;
                ArrayList<UserAttributeForm> attrList;

                myNewPwdFlow = event.getFlow();
                attrList = getUserAttributes(myNewPwdFlow);
                intent = new Intent(LoginActivityBase.this,
                        NewPasswordActivity.class);
                intent.putParcelableArrayListExtra(
                        CognitoAdapter.PROP_USERATTRIBUTES, attrList);
                startActivityForResult(intent,
                        LoginActivityBase.REQ_NEWPASSWORD);
            }
        });
    }

    /**
     * Returns the user attributes for the new password process.
     *
     * @param  flow State of the flow.
     * @return      The collection.
     */
    private ArrayList<UserAttributeForm> getUserAttributes(
            NewPasswordContinuation flow) {
        int currCount, reqCount;
        UserAttributeForm attr;
        List<String> reqAttrs;
        Map<String, String> currAttrs;
        Map<String, UserAttributeForm> attrs;

        reqAttrs = flow.getRequiredAttributes();
        reqCount = (reqAttrs == null) ? 0 : reqAttrs.size();
        currAttrs = flow.getCurrentUserAttributes();
        currCount = (currAttrs == null) ? 0 : currAttrs.size();
        if (reqCount == 0 && currCount == 0) {
            return new ArrayList<>(0);
        }

        attrs = new LinkedHashMap<>((reqCount > currCount) ? reqCount :
                currCount);
        if (reqCount > 0) {
            // Required attributes
            for (String key : reqAttrs) {
                myLogger.debug("required attribute: {}", key);
                attr = new UserAttributeForm(key, true);
                attrs.put(key, attr);
            }
        }

        if (currCount > 0) {
            // Merge current attributes
            String key;

            for (Map.Entry<String, String> entry : currAttrs.entrySet()) {
                key = entry.getKey();
                myLogger.debug("current attribute: {}={}", key,
                        entry.getValue());

                if (UserAttribute.ATTR_EMAIL_VERIFIED.equals(key) ||
                        UserAttribute.ATTR_PHONENUMBER_VERIFIED.equals(key)) {
                    continue;
                }

                attr = attrs.get(key);
                if (attr == null) {
                    attr = new UserAttributeForm(key, false);
                    attrs.put(key, attr);
                }

                attr.setCurrentValue(entry.getValue());
            }
        }

        return new ArrayList<>(attrs.values());
    }

    /**
     * Handles the result of the new password process.
     *
     * @param resultCode The result code.
     * @param data       The result data.
     */
    private void onNewPasswordResult(int resultCode, Intent data) {
        SecureString pwd = null;
        Disposable connection;
        ContinueObservable process;
        SingleCoordinator<Object> coordinator;
        byte[] buf = null;

        try {
            if (myNewPwdFlow == null) {
                throw new NullPointerException(
                        "No current flow for requesting new password.");
            }

            buf = (resultCode == Activity.RESULT_OK) ?
                    data.getByteArrayExtra(CognitoAdapter.PROP_PASSWORDNEW) :
                    null;
            pwd = new SecureString(buf);

            if (TextUtils.isEmpty(pwd)) {
                throw new ApplicationException.Builder(
                        R.string.it_scoppelletti_cognito_msg_newPassword)
                        .build();
            }

            updateUserAttributes(myNewPwdFlow,
                    data.<UserAttributeForm>getParcelableArrayListExtra(
                    CognitoAdapter.PROP_USERATTRIBUTES));

            // Amazon Cognito uses immutable strings for passwords
            myNewPwdFlow.setPassword(pwd.toString());

            myProgressBar.show();
            coordinator = AppExt.getOrCreateFragment(this,
                    LoginActivityData.class, CognitoAdapter.TAG_LOGINDATA)
                    .getLoginCoordinator();
            if (coordinator.isRunning()) {
                throw new ApplicationException.Builder(
                        R.string.it_scoppelletti_cognito_err_loginAlreadyInProgress)
                        .title(R.string.it_scoppelletti_cmd_login).build();
            }

            process = new ContinueObservable(myNewPwdFlow);
            connection = coordinator.connect(Observable.create(process)
                    .firstOrError()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()));
            myDisposables.add(connection);
        } catch (RuntimeException ex) {
            EventBus.getDefault().post(new ExceptionEvent(ex)
                .title(R.string.it_scoppelletti_cmd_login));
        } finally {
            myNewPwdFlow = null;
            if (pwd != null) {
                pwd.clear();
            }
            if (buf != null) {
                Arrays.fill(buf, (byte) 0);
            }
        }
    }

    /**
     * Updates the user attributes.
     *
     * @param flow  State of the change password flow.
     * @param attrs User attributes.
     */
    private void updateUserAttributes(NewPasswordContinuation flow,
            List<UserAttributeForm> attrs) {
        String key, value;

        if (attrs == null || attrs.isEmpty()) {
             return;
        }

        for (UserAttributeForm form : attrs) {
            value = form.getEditingValue();
            if (!TextUtils.equals(value, form.getCurrentValue())) {
                key = form.getKey();

                myLogger.debug("Updating attribute {} to value {}.", key,
                        value);
                flow.setUserAttribute(key, value);
            }
        }
    }

    /**
     * Performs the reset password process.
     */
    private void onResetPasswordClick() {
        LoginForm form;
        Disposable connection;
        ResetPasswordObservable process;
        SingleCoordinator<Object> coordinator;

        AppExt.hideSoftKeyboard(this);

        form = myBinding.getForm();
        if (!form.validateForgotPassword()) {
            return;
        }

        myProgressBar.show();
        try {
            coordinator = AppExt.getOrCreateFragment(this,
                    LoginActivityData.class, CognitoAdapter.TAG_LOGINDATA)
                    .getResetPasswordCoordinator();
            if (coordinator.isRunning()) {
                throw new ApplicationException.Builder(
                        R.string.it_scoppelletti_cognito_err_resetPasswordAlreadyInProgress)
                        .title(R.string.it_scoppelletti_cmd_resetPassword).build();
            }

            process = new ResetPasswordObservable(form.getUserCode());
            connection = coordinator.connect(Observable.create(process)
                    .firstOrError()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()));
            myDisposables.add(connection);
        } catch (RuntimeException ex) {
            EventBus.getDefault().post(new ExceptionEvent(ex)
                    .title(R.string.it_scoppelletti_cmd_resetPassword));
        }
    }

    /**
     * Prompts the user for the validation code and a new password.
     *
     * @param event The event.
     */
    @Subscribe
    public void onResetPasswordPrompt(
            final @NonNull ResetPasswordEvent event) {
        if (event == null) {
            throw new NullPointerException("Argument event is null.");
        }

        myProgressBar.hide(new Runnable() {

            @Override
            public void run() {
                Intent intent;

                myResetPwdFlow = event.getFlow();
                intent = new Intent(LoginActivityBase.this,
                        ResetPasswordActivity.class);
                intent.putExtra(CognitoAdapter.PROP_DELIVERYMEDIUM,
                        myResetPwdFlow.getParameters().getDeliveryMedium());
                intent.putExtra(CognitoAdapter.PROP_DESTINATION,
                        myResetPwdFlow.getParameters().getDestination());
                startActivityForResult(intent,
                        LoginActivityBase.REQ_RESETPASSWORD);
            }
        });
    }

    /**
     * Handles the result of the reset password process.
     *
     * @param resultCode The result code.
     * @param data       The result data.
     */
    private void onResetPasswordResult(int resultCode, Intent data) {
        SecureString pwd = null;
        SecureString checkCode = null;
        Disposable connection;
        ContinueObservable process;
        SingleCoordinator<Object> coordinator;
        byte[] buf = null;

        try {
            if (myResetPwdFlow == null) {
                throw new NullPointerException(
                        "No current flow for resetting password.");
            }

            buf = (resultCode == Activity.RESULT_OK) ?
                    data.getByteArrayExtra(
                            CognitoAdapter.PROP_VERIFICATIONCODE) : null;
            checkCode = new SecureString(buf);
            if (buf != null) {
                Arrays.fill(buf, (byte) 0);
                buf = null;
            }

            buf = (resultCode == Activity.RESULT_OK) ?
                    data.getByteArrayExtra(CognitoAdapter.PROP_PASSWORDNEW) :
                    null;
            pwd = new SecureString(buf);

            if (TextUtils.isEmpty(checkCode) || TextUtils.isEmpty(pwd)) {
                throw new ApplicationException.Builder(
                        R.string.it_scoppelletti_cognito_msg_newPassword)
                        .build();
            }

            // Amazon Cognito uses immutable strings for passwords
            myResetPwdFlow.setVerificationCode(checkCode.toString());
            myResetPwdFlow.setPassword(pwd.toString());

            myProgressBar.show();
            coordinator = AppExt.getOrCreateFragment(this,
                    LoginActivityData.class, CognitoAdapter.TAG_LOGINDATA)
                    .getResetPasswordCoordinator();
            if (coordinator.isRunning()) {
                throw new ApplicationException.Builder(
                        R.string.it_scoppelletti_cognito_err_resetPasswordAlreadyInProgress)
                        .title(R.string.it_scoppelletti_cmd_resetPassword)
                        .build();
            }

            process = new ContinueObservable(myResetPwdFlow);
            connection = coordinator.connect(Observable.create(process)
                    .firstOrError()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()));
            myDisposables.add(connection);
        } catch (RuntimeException ex) {
            EventBus.getDefault().post(new ExceptionEvent(ex)
                    .title(R.string.it_scoppelletti_cmd_resetPassword));
        } finally {
            myResetPwdFlow = null;
            if (pwd != null) {
                pwd.clear();
            }
            if (buf != null) {
                Arrays.fill(buf, (byte) 0);
            }
        }
    }
}
