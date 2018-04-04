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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import it.scoppelletti.spaceship.app.AppExt;
import it.scoppelletti.spaceship.cognito.CognitoAdapter;
import it.scoppelletti.spaceship.cognito.R;
import it.scoppelletti.spaceship.cognito.data.NewPasswordViewModel;
import it.scoppelletti.spaceship.cognito.data.UserAttributeViewModel;
import it.scoppelletti.spaceship.cognito.databinding.NewPasswordActivityBinding;
import it.scoppelletti.spaceship.cognito.widget.UserAttributeView;

/**
 * Activity to prompt the user for a new password.
 *
 * @since 1.0.0
 */
public final class NewPasswordActivity extends AppCompatActivity {
    private static final String PROP_MODEL = "1";
    private NewPasswordActivityBinding myBinding;
    private ArrayList<UserAttributeViewModel> myAttrList;
    private List<UserAttributeView> myAttrViews;

    /**
     * Sole constructor.
     */
    public NewPasswordActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toolbar toolbar;
        NewPasswordViewModel model;

        super.onCreate(savedInstanceState);
        myBinding = DataBindingUtil.setContentView(this,
                R.layout.it_scoppelletti_cognito_newpassword_activity);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_menu);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            model = new NewPasswordViewModel();
            myAttrList = getIntent().getParcelableArrayListExtra(
                    CognitoAdapter.PROP_USERATTRIBUTES);
        } else {
            model = savedInstanceState.getParcelable(
                    NewPasswordActivity.PROP_MODEL);
            myAttrList = savedInstanceState.getParcelableArrayList(
                    CognitoAdapter.PROP_USERATTRIBUTES);
        }

        myBinding.setModel(model);
        myBinding.txtPasswordConfirm.setOnEditorActionListener(
                (view, actionId, event) ->
                        NewPasswordActivity.this.onEditorAction(actionId));

        if (myAttrList == null || myAttrList.isEmpty()) {
            myAttrViews = null;
        } else {
            bindAttrs();
        }

        toolbar.setNavigationOnClickListener((view) -> onCancelClick());
    }

    /**
     * Creates the UI for the user attributes.
     */
    private void bindAttrs() {
        ViewGroup parent;
        UserAttributeView view;
        LinearLayout.LayoutParams layout;

        myAttrViews = new ArrayList<>();
        parent = findViewById(R.id.form_frame);

        for (UserAttributeViewModel form : myAttrList) {
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
        NewPasswordViewModel model;

        super.onSaveInstanceState(outState);

        if (myBinding != null) {
            model = myBinding.getModel();
            if (model != null) {
                outState.putParcelable(NewPasswordActivity.PROP_MODEL, model);
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
        NewPasswordViewModel model;

        AppExt.hideSoftKeyboard(this);

        model = myBinding.getModel();
        valid = model.validate();

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

        data = new Intent();
        data.putExtra(CognitoAdapter.PROP_PASSWORDNEW, model.getPasswordNew());

        if (myAttrList != null) {
            data.putParcelableArrayListExtra(
                    CognitoAdapter.PROP_USERATTRIBUTES, myAttrList);
        }

        setResult(Activity.RESULT_OK, data);
        finish();
    }

    /**
     * Cancels the activity.
     */
    private void onCancelClick() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}
