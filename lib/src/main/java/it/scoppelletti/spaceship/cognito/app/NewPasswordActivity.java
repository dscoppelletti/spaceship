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
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;
import it.scoppelletti.spaceship.cognito.CognitoAdapter;
import it.scoppelletti.spaceship.cognito.R;
import it.scoppelletti.spaceship.cognito.data.NewPasswordForm;
import it.scoppelletti.spaceship.cognito.data.UserAttributeForm;
import it.scoppelletti.spaceship.cognito.databinding.NewPasswordActivityBinding;
import it.scoppelletti.spaceship.cognito.widget.UserAttributeView;
import it.scoppelletti.spaceship.security.SecureString;

/**
 * Activity to prompt the user for a new password.
 *
 * @since 1.0.0
 */
public final class NewPasswordActivity extends AppCompatActivity {
    private static final String PROP_FORM = "1";
    private NewPasswordActivityBinding myBinding;
    private ArrayList<UserAttributeForm> myAttrList;
    private List<UserAttributeView> myAttrViews;

    /**
     * Sole constructor.
     */
    public NewPasswordActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toolbar toolbar;
        ActionBar actionBar;
        NewPasswordForm form;

        super.onCreate(savedInstanceState);
        myBinding = DataBindingUtil.setContentView(this,
                R.layout.it_scoppelletti_cognito_newpassword_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        if (savedInstanceState == null) {
            form = new NewPasswordForm();
            myAttrList = getIntent().getParcelableArrayListExtra(
                    CognitoAdapter.PROP_USERATTRIBUTES);
        } else {
            form = savedInstanceState.getParcelable(
                    NewPasswordActivity.PROP_FORM);
            myAttrList = savedInstanceState.getParcelableArrayList(
                    CognitoAdapter.PROP_USERATTRIBUTES);
        }

        myBinding.setForm(form);
        myBinding.txtPasswordConfirm.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {

                    @Override
                    public boolean onEditorAction(@NonNull TextView view,
                            int actionId, @Nullable KeyEvent event) {
                        return NewPasswordActivity.this.onEditorAction(
                                actionId);
                    }
                });

        if (myAttrList == null || myAttrList.isEmpty()) {
            myAttrViews = null;
        } else {
            bindAttrs();
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onCancelClick();
            }
        });
    }

    /**
     * Creates the UI for the user attributes.
     */
    private void bindAttrs() {
        ViewGroup parent;
        UserAttributeView view;
        LinearLayout.LayoutParams layout;

        myAttrViews = new ArrayList<>();
        parent = (ViewGroup) findViewById(R.id.form_frame);

        for (UserAttributeForm form : myAttrList) {
            view = new UserAttributeView(this);
            layout = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            parent.addView(view, layout);
            view.bind(form);
            myAttrViews.add(view);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        NewPasswordForm form;

        super.onSaveInstanceState(outState);

        if (myBinding != null) {
            form = myBinding.getForm();
            if (form != null) {
                outState.putParcelable(NewPasswordActivity.PROP_FORM, form);
            }
        }

        if (myAttrList != null) {
            outState.putParcelableArrayList(CognitoAdapter.PROP_USERATTRIBUTES,
                    myAttrList);
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
        boolean valid;
        Intent data;
        SecureString pwd;
        NewPasswordForm form;

        form = myBinding.getForm();
        valid = form.validate();

        if (myAttrViews != null) {
            for (UserAttributeView view : myAttrViews) {
                if (!view.validate()) {
                    valid = false;
                }
            }
        }
        if (!valid) {
            return;
        }

        pwd = new SecureString(form.getPasswordNew());

        try {
            data = new Intent();
            data.putExtra(CognitoAdapter.PROP_PASSWORDNEW, pwd.toByteArray());

            if (myAttrList != null) {
                data.putParcelableArrayListExtra(
                        CognitoAdapter.PROP_USERATTRIBUTES, myAttrList);
            }

            setResult(Activity.RESULT_OK, data);
            finish();
        } finally {
            pwd.clear();
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
