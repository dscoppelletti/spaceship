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

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
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
import it.scoppelletti.spaceship.cognito.CognitoAdapter;
import it.scoppelletti.spaceship.cognito.R;
import it.scoppelletti.spaceship.cognito.data.ResetPasswordForm;
import it.scoppelletti.spaceship.cognito.databinding.ResetPasswordActivityBinding;
import it.scoppelletti.spaceship.security.SecureString;
import it.scoppelletti.spaceship.types.StringExt;

/**
 * Activity to prompt the user for the validation code and a new password.
 *
 * @since 1.0.0
 */
public final class ResetPasswordActivity extends AppCompatActivity {
    private static final String PROP_FORM = "1";
    private ResetPasswordActivityBinding myBinding;

    /**
     * Sole constructor.
     */
    public ResetPasswordActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String deliveryMedium, destination, msg;
        Toolbar toolbar;
        ActionBar actionBar;
        ResetPasswordForm form;

        super.onCreate(savedInstanceState);
        myBinding = DataBindingUtil.setContentView(this,
                R.layout.it_scoppelletti_cognito_resetpassword_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        if (savedInstanceState == null) {
            form = new ResetPasswordForm();
        } else {
            form = savedInstanceState.getParcelable(
                    ResetPasswordActivity.PROP_FORM);
        }

        deliveryMedium = getIntent().getStringExtra(
                CognitoAdapter.PROP_DELIVERYMEDIUM);
        destination = getIntent().getStringExtra(
                CognitoAdapter.PROP_DESTINATION);
        if (TextUtils.isEmpty(deliveryMedium) ||
                TextUtils.isEmpty(destination)) {
            msg = StringExt.EMPTY;
        } else {
            msg = getString(
                    R.string.it_scoppelletti_cognito_msg_verificationCode,
                    deliveryMedium, destination);
        }

        myBinding.msgVerificationCode.setText(msg);
        myBinding.setForm(form);
        myBinding.txtPasswordConfirm.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {

                    @Override
                    public boolean onEditorAction(@NonNull TextView view,
                            int actionId, @Nullable KeyEvent event) {
                        return ResetPasswordActivity.this.onEditorAction(
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        ResetPasswordForm form;

        super.onSaveInstanceState(outState);

        if (myBinding != null) {
            form = myBinding.getForm();
            if (form != null) {
                outState.putParcelable(ResetPasswordActivity.PROP_FORM, form);
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
        Intent data;
        SecureString pwd = null;
        SecureString validCode = null;
        ResetPasswordForm form;

        form = myBinding.getForm();
        if (!form.validate()) {
            return;
        }

        try {
            validCode = new SecureString(form.getVerificationCode());
            pwd = new SecureString(form.getPasswordNew());
            data = new Intent();
            data.putExtra(CognitoAdapter.PROP_VERIFICATIONCODE,
                    validCode.toByteArray());
            data.putExtra(CognitoAdapter.PROP_PASSWORDNEW, pwd.toByteArray());
            setResult(Activity.RESULT_OK, data);
            finish();
        } finally {
            if (validCode != null) {
                validCode.clear();
            }
            if (pwd != null) {
                pwd.clear();
            }
        }
    }

    /**
     * Cancels the activity.
     */
    private void onCancelClick() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}
