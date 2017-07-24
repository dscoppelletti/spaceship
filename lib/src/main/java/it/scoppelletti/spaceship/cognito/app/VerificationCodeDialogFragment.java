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

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import org.greenrobot.eventbus.EventBus;
import it.scoppelletti.spaceship.app.DialogCloseEvent;
import it.scoppelletti.spaceship.cognito.CognitoAdapter;
import it.scoppelletti.spaceship.cognito.R;

/**
 * Dialog to prompts the user if her have the verification code or not.
 *
 * @since 1.0.0
 */
public final class VerificationCodeDialogFragment extends
        AppCompatDialogFragment {

    /**
     * The fragment tag.
     */
    public static final String TAG = CognitoAdapter.TAG_VERIFICATIONCODEDIALOG;

    private static final String PROP_TITLEID = "1";
    private static final String PROP_REQCODE = "2";

    /**
     * Sole constructor.
     */
    public VerificationCodeDialogFragment() {
    }

    /**
     * Creates a new fragment instance.
     *
     * @param  titleId The title as a string resource ID.
     * @param  reqCode The request code.
     * @return         The new object.
     */
    static VerificationCodeDialogFragment newInstance(@StringRes int titleId,
            int reqCode) {
        Bundle args;
        VerificationCodeDialogFragment fragment;

        args = new Bundle();
        args.putInt(VerificationCodeDialogFragment.PROP_TITLEID, titleId);
        args.putInt(VerificationCodeDialogFragment.PROP_REQCODE, reqCode);

        fragment = new VerificationCodeDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        int titleId;
        Bundle args;
        AlertDialog.Builder builder;

        args = getArguments();
        titleId = args.getInt(VerificationCodeDialogFragment.PROP_TITLEID);

        builder = new AlertDialog.Builder(getActivity())
                .setTitle(titleId)
                .setItems(
                        R.array.it_scoppelletti_cognito_array_verificationCode,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                post((which == 0) ?
                                        DialogInterface.BUTTON_POSITIVE :
                                        DialogInterface.BUTTON_NEGATIVE);
                            }
                        });

        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        post(DialogInterface.BUTTON_NEGATIVE);
    }

    /**
     * Posts the dialog result.
     *
     * @param which The button that was clicked.
     */
    private void post(int which) {
        int reqCode;
        Bundle args;
        DialogCloseEvent event;

        args = getArguments();
        reqCode = args.getInt(VerificationCodeDialogFragment.PROP_REQCODE);
        event = new DialogCloseEvent(reqCode);
        event.setResult(which);
        EventBus.getDefault().post(event);
    }
}
