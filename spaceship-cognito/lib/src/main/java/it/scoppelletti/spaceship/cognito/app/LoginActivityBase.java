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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.NewPasswordContinuation;
import io.reactivex.Completable;
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
import it.scoppelletti.spaceship.cognito.R;
import it.scoppelletti.spaceship.cognito.data.LoginViewModel;
import it.scoppelletti.spaceship.cognito.data.SpaceshipUser;
import it.scoppelletti.spaceship.cognito.data.UserAttribute;
import it.scoppelletti.spaceship.cognito.data.UserAttributeViewModel;
import it.scoppelletti.spaceship.cognito.databinding.LoginActivityBinding;
import it.scoppelletti.spaceship.rx.CompletableCoordinator;
import it.scoppelletti.spaceship.rx.CompleteEvent;
import it.scoppelletti.spaceship.rx.MaybeCoordinator;
import it.scoppelletti.spaceship.rx.SingleCoordinator;
import it.scoppelletti.spaceship.widget.ProgressOverlay;
import it.scoppelletti.spaceship.widget.SnackbarEvent;

/**
 * Base class for a login activity.
 *
 * @since 1.0.0
 */
@Slf4j
public abstract class LoginActivityBase extends AppCompatActivity {
    private static final int ACTION_GETCURRENTUSER = 1;
    private static final int ACTION_LOGOUT = 2;
    private static final int REQ_NEWPASSWORD = 1;
    private static final String PROP_MODEL = "1";
    private int myFirstRun;
    private ProgressOverlay myProgressBar;
    private LoginActivityBinding myBinding;
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
        LoginViewModel model;

        super.onCreate(savedInstanceState);
        myBinding = DataBindingUtil.setContentView(this,
                R.layout.it_scoppelletti_cognito_login_activity);
        myDisposables = new CompositeDisposable();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myProgressBar = findViewById(R.id.progress_bar);

        if (savedInstanceState == null) {
            if (getIntent().getBooleanExtra(CognitoAdapter.PROP_LOGOUT,
                    false)) {
                myFirstRun = LoginActivityBase.ACTION_LOGOUT;
            } else {
                myFirstRun = LoginActivityBase.ACTION_GETCURRENTUSER;
            }

            model = new LoginViewModel();
        } else {
            myFirstRun = 0;
            model = savedInstanceState.getParcelable(
                    LoginActivityBase.PROP_MODEL);
        }

        myBinding.setModel(model);
        myBinding.txtPassword.setOnEditorActionListener(
                (view, actionId, event) ->
                        LoginActivityBase.this.onEditorAction(actionId));

        cmd = findViewById(R.id.cmd_login);
        cmd.setOnClickListener((view) -> onLoginClick());

        cmd = findViewById(R.id.cmd_forgotPassword);
        cmd.setOnClickListener((view) -> onForgotPasswordClick());
    }

    @Override
    protected void onResume() {
        Disposable subscription;
        LoginActivityData data;
        SingleCoordinator<Object> loginCoordinator;
        MaybeCoordinator<CognitoUser> currentUserCoordinator;
        SingleCoordinator<GetUserDetailsEvent> userDetailCoordinator;
        CompletableCoordinator logoutCoordinator;

        super.onResume();
        EventBus.getDefault().register(this);

        data = AppExt.getOrCreateFragment(this, LoginActivityData.class,
                LoginActivityData.TAG);
        loginCoordinator = data.getLoginCoordinator();
        subscription = loginCoordinator.subscribe(() -> new LoginObserver());
        myDisposables.add(subscription);

        currentUserCoordinator = data.getCurrentUserCoordinator();
        subscription = currentUserCoordinator.subscribe(() ->
                        new GetCurrentUserObserver());
        myDisposables.add(subscription);

        userDetailCoordinator = data.getUserDetailsCoordinator();
        subscription = userDetailCoordinator.subscribe(() ->
                        new GetUserDetailsObserver());
        myDisposables.add(subscription);

        logoutCoordinator = data.getLogoutCoordinator();
        subscription = logoutCoordinator.subscribe(() -> new LogoutObserver());
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

        try {
            switch (myFirstRun) {
            case LoginActivityBase.ACTION_GETCURRENTUSER:
                getCurrentUser(currentUserCoordinator);
                break;

            case LoginActivityBase.ACTION_LOGOUT:
                logout(logoutCoordinator);
                break;
            }
        } finally {
            myFirstRun = 0;
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
        LoginViewModel model;

        super.onSaveInstanceState(outState);

        if (myBinding != null) {
            model = myBinding.getModel();
            if (model != null) {
                outState.putParcelable(LoginActivityBase.PROP_MODEL, model);
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
    public void onSnackbarEvent(@NonNull final SnackbarEvent event) {
        if (event == null) {
            throw new NullPointerException("Argument event is null.");
        }

        myProgressBar.hide(() -> event.show(myBinding.contentFrame));
    }

    /**
     * Handles an exception.
     *
     * @param event The event.
     */
    @Subscribe
    public void onExceptionEvent(@NonNull final ExceptionEvent event) {
        myProgressBar.hide(() ->
                new ExceptionDialogFragment.Builder(LoginActivityBase.this)
                        .exceptionEvent(event)
                        .show());
    }

    /**
     * Retrieves the current user.
     *
     * @param coordinator Coordinator.
     */
    private void getCurrentUser(MaybeCoordinator<CognitoUser> coordinator) {
        Disposable connection;
        GetCurrentUserObservable process;

        myProgressBar.show();
        try {
            process = new GetCurrentUserObservable();
            connection = coordinator.connect(Observable.create(process)
                    .firstElement()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()));
            myDisposables.add(connection);
        } catch (RuntimeException ex) {
            myProgressBar.hide();
            myLogger.error("Failed to get the current user.", ex);
        }
    }

    /**
     * Performs the logout process.
     *
     * @param coordinator Coordinator.
     */
    private void logout(CompletableCoordinator coordinator) {
        Disposable connection;

        myProgressBar.show();
        try {
            connection = coordinator.connect(Completable.fromRunnable(() ->
                    CognitoAdapter.getInstance().logout())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()));
            myDisposables.add(connection);
        } catch (RuntimeException ex) {
            myProgressBar.hide();
            myLogger.error("Failed to logout.", ex);
        }
    }

    /**
     * Performs the login process.
     */
    private void onLoginClick() {
        LoginViewModel model;
        Disposable connection;
        LoginObservable process;
        SingleCoordinator<Object> coordinator;

        AppExt.hideSoftKeyboard(this);

        model = myBinding.getModel();
        if (!model.validateLogin()) {
            return;
        }

        myProgressBar.show();
        try {
            coordinator = AppExt.getOrCreateFragment(this,
                    LoginActivityData.class, LoginActivityData.TAG)
                    .getLoginCoordinator();
            if (coordinator.isRunning()) {
                throw new ApplicationException.Builder(
                        R.string.it_scoppelletti_cognito_err_loginAlreadyInProgress)
                        .title(R.string.it_scoppelletti_cmd_login).build();
            }

            process = new LoginObservable(model.getUserCode(),
                    model.getPassword());
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
     * @param user The logged user.
     */
    @Subscribe
    public void onLoginEvent(@NonNull CognitoUser user) {
        Disposable connection;
        LoginActivityData data;
        GetUserDetailsObservable process;
        SingleCoordinator<GetUserDetailsEvent> coordinator;

        if (user == null) {
            throw new NullPointerException("Argument user is null.");
        }

        data = AppExt.getOrCreateFragment(this,
                LoginActivityData.class, LoginActivityData.TAG);
        data.setPendingUser(user);

        myProgressBar.show();
        try {
            coordinator = data.getUserDetailsCoordinator();
            process = new GetUserDetailsObservable(user);
            connection = coordinator.connect(Observable.create(process)
                    .firstOrError()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()));
            myDisposables.add(connection);
        } catch (RuntimeException ex) {
            myLogger.error(String.format("Failed to load details of user %1$s.",
                    user.getUserId()), ex);
            onGetUserDetails(new GetUserDetailsEvent(null));
        }
    }

    /**
     * Called when the details of the logged user has been loaded.
     *
     * @param event The event.
     */
    @Subscribe
    public void onGetUserDetails(@NonNull final GetUserDetailsEvent event) {
        LoginActivityData data;
        final CognitoUser user;

        if (event == null) {
            throw new NullPointerException("Argument event is null.");
        }

        data = AppExt.getOrCreateFragment(this,
                LoginActivityData.class, LoginActivityData.TAG);
        user = data.getPendingUser();
        if (user == null) {
            throw new NullPointerException("No pending user.");
        }

        data.setPendingUser(null);
        myProgressBar.hide(() -> {
            CognitoAdapter.getInstance().setCurrentUser(
                    new SpaceshipUser(user, event.getData()));
            onLoginSucceeded();
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
    public void onNewPasswordPrompt(@NonNull final NewPasswordEvent event) {
        LoginActivityData data;
        final Intent intent;
        final ArrayList<UserAttributeViewModel> attrList;

        if (event == null) {
            throw new NullPointerException("Argument event is null.");
        }

        data = AppExt.getOrCreateFragment(this,
                LoginActivityData.class, LoginActivityData.TAG);
        data.setNewPasswordEvent(event);
        attrList = getUserAttributes(event.getFlow());
        intent = new Intent(this, NewPasswordActivity.class);
        intent.putParcelableArrayListExtra(
                CognitoAdapter.PROP_USERATTRIBUTES, attrList);
        myProgressBar.hide(() -> startActivityForResult(intent,
                LoginActivityBase.REQ_NEWPASSWORD));
    }

    /**
     * Returns the user attributes for the new password process.
     *
     * @param  flow State of the flow.
     * @return      The collection.
     */
    private ArrayList<UserAttributeViewModel> getUserAttributes(
            NewPasswordContinuation flow) {
        int currCount, reqCount;
        UserAttributeViewModel attr;
        List<String> reqAttrs;
        Map<String, String> currAttrs;
        Map<String, UserAttributeViewModel> attrs;

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
                attr = new UserAttributeViewModel(key, true);
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
                    attr = new UserAttributeViewModel(key, false);
                    attrs.put(key, attr);
                }

                attr.setCurrentValue(entry.getValue());
            }
        }

        // - Amazon Cognito Indentity Provider 2.4.5
        // I think user code should be preset
        if (myBinding != null) {
            attr = attrs.get(UserAttribute.ATTR_USERCODE);
            if (attr != null && TextUtils.isEmpty(attr.getCurrentValue())) {
                attr.setEditingValue(myBinding.getModel().getUserCode());
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
        String pwd;
        Disposable connection;
        LoginActivityData activityData;
        NewPasswordEvent event;
        NewPasswordObservable process;
        NewPasswordContinuation flow;
        SingleCoordinator<Object> coordinator;

        activityData = AppExt.getOrCreateFragment(this,
                LoginActivityData.class, LoginActivityData.TAG);

        myProgressBar.show();
        try {
            event = activityData.getNewPasswordEvent();
            if (event == null) {
                throw new NullPointerException(
                        "No pending request for new password.");
            }

            flow = event.getFlow();
            pwd = (resultCode == Activity.RESULT_OK) ?
                    data.getStringExtra(CognitoAdapter.PROP_PASSWORDNEW) :
                    null;
            if (TextUtils.isEmpty(pwd)) {
                throw new ApplicationException.Builder(
                        R.string.it_scoppelletti_cognito_msg_newPassword)
                        .build();
            }

            updateUserAttributes(flow, data.getParcelableArrayListExtra(
                    CognitoAdapter.PROP_USERATTRIBUTES));
            flow.setPassword(pwd);

            coordinator = AppExt.getOrCreateFragment(this,
                    LoginActivityData.class, LoginActivityData.TAG)
                    .getLoginCoordinator();
            if (coordinator.isRunning()) {
                throw new ApplicationException.Builder(
                        R.string.it_scoppelletti_cognito_err_loginAlreadyInProgress)
                        .title(R.string.it_scoppelletti_cmd_login).build();
            }

            process = new NewPasswordObservable(event);
            connection = coordinator.connect(Observable.create(process)
                    .firstOrError()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()));
            myDisposables.add(connection);
        } catch (RuntimeException ex) {
            EventBus.getDefault().post(new ExceptionEvent(ex)
                .title(R.string.it_scoppelletti_cmd_login));
        } finally {
            activityData.setNewPasswordEvent(null);
        }
    }

    /**
     * Updates the user attributes.
     *
     * @param flow  State of the change password flow.
     * @param attrs User attributes.
     */
    private void updateUserAttributes(NewPasswordContinuation flow,
            List<UserAttributeViewModel> attrs) {
        String key, value;

        if (attrs == null || attrs.isEmpty()) {
             return;
        }

        for (UserAttributeViewModel form : attrs) {
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
     * Forgot password.
     */
    private void onForgotPasswordClick() {
        Intent intent;
        LoginViewModel model;

        AppExt.hideSoftKeyboard(this);

        model = myBinding.getModel();
        if (!model.validateForgotPassword()) {
            return;
        }

        intent = new Intent(this, ForgotPasswordActivity.class);
        intent.putExtra(CognitoAdapter.PROP_USERCODE, model.getUserCode());
        startActivity(intent);
    }
}
