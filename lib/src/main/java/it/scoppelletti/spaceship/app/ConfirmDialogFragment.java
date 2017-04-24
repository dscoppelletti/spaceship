/*
 * Copyright (C) 2013-2016 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.EventBus;

/**
 * Confirmation dialog.
 *
 * @since 1.0.0
 */
@Slf4j
@UiThread
public final class ConfirmDialogFragment extends AppCompatDialogFragment {

    /**
     * The fragment tag.
     */
    public static final String TAG = AppExt.TAG_CONFIRMDIALOG;

    private static final String PROP_TITLEID = "1";
    private static final String PROP_POSITIVEID = "2";
    private static final String PROP_NEGATIVEID = "3";
    private static final String PROP_MSG = "4";
    private static final String PROP_MSGID = "5";
    private static final String PROP_CLOSEEVENT = "6";

    /**
     * Sole constructor.
     */
    public ConfirmDialogFragment() {
    }

    /**
     * Creates a new fragment instance.
     *
     * @param  builder The instance builder.
     * @return         The new object.
     */
    private static ConfirmDialogFragment newInstance(
            ConfirmDialogFragment.Builder builder) {
        ConfirmDialogFragment fragment;

        if (builder.myMsgArgs == null) {
            builder.myDialogArgs.remove(ConfirmDialogFragment.PROP_MSG);
            builder.myDialogArgs.putInt(ConfirmDialogFragment.PROP_MSGID,
                    builder.myMsgId);
        } else {
            builder.myDialogArgs.remove(ConfirmDialogFragment.PROP_MSGID);
            builder.myDialogArgs.putString(ConfirmDialogFragment.PROP_MSG,
                    builder.myActivity.getString(builder.myMsgId,
                            builder.myMsgArgs.toArray()));
        }

        fragment = new ConfirmDialogFragment();
        fragment.setArguments(builder.myDialogArgs);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        int negativeId, positiveId, titleId;
        Bundle args;
        AlertDialog.Builder builder;
        Activity activity;

        activity = getActivity();
        args = getArguments();
        titleId = args.getInt(ConfirmDialogFragment.PROP_TITLEID);
        positiveId = args.getInt(ConfirmDialogFragment.PROP_POSITIVEID,
                android.R.string.ok);
        negativeId = args.getInt(ConfirmDialogFragment.PROP_NEGATIVEID,
                android.R.string.cancel);

        builder = new AlertDialog.Builder(activity)
                .setTitle(titleId)
                .setPositiveButton(positiveId,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                post(DialogInterface.BUTTON_POSITIVE);
                            }
                        })
                .setNegativeButton(negativeId,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                post(DialogInterface.BUTTON_NEGATIVE);
                            }
                        });

        setMessage(builder, args);

        return builder.create();
    }

    /**
     * Sets the message.
     *
     * @param builder The dialog builder.
     * @param args    The fragment arguments.
     */
    private void setMessage(AlertDialog.Builder builder, Bundle args) {
        int msgId;
        String msg;

        msgId = args.getInt(ConfirmDialogFragment.PROP_MSGID, -1);
        if (msgId > 0) {
            builder.setMessage(msgId);
            return;
        }

        msg = args.getString(ConfirmDialogFragment.PROP_MSG);
        if (!TextUtils.isEmpty(msg)) {
            builder.setMessage(msg);
        }
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
        Bundle args;
        DialogCloseEvent req;

        args = getArguments();
        req = args.getParcelable(ConfirmDialogFragment.PROP_CLOSEEVENT);
        if (req != null) {
            req.setResult(which);
            EventBus.getDefault().post(req);
        } else {
            myLogger.info("Dialog closed with result {}.", which);
        }
    }

    /**
     * Builds a {@code ConfirmDialogFragment} instance.
     *
     * @since 1.0.0
     */
    public static final class Builder {
        private final int myMsgId;
        private final FragmentActivity myActivity;
        private final Bundle myDialogArgs;
        private List<Object> myMsgArgs;

        /**
         * Constructor.
         *
         * @param activity The activity.
         * @param msgId    The message as a string resource ID.
         */
        public Builder(@NonNull Activity activity, @StringRes int msgId) {
            if (activity == null) {
                throw new NullPointerException("Argument activity is null.");
            }

            myActivity = (FragmentActivity) activity;
            myMsgId = msgId;
            myDialogArgs = new Bundle();
        }

        /**
         * Sets the title.
         *
         * @param  value The value as a string resource ID.
         * @return       This object.
         */
        @NonNull
        public ConfirmDialogFragment.Builder title(@StringRes int value) {
            myDialogArgs.putInt(ConfirmDialogFragment.PROP_TITLEID, value);
            return this;
        }

        /**
         * Sets the message arguments.
         *
         * @param  v The array of arguments.
         * @return   This object.
         */
        @NonNull
        public ConfirmDialogFragment.Builder messageArguments(Object... v) {
            myMsgArgs = (v == null) ? null : Arrays.asList(v);
            return this;
        }

        /**
         * Adds a message argument.
         *
         * @param  obj The object. May be {@code null}.
         * @return     This object.
         */
        @NonNull
        public ConfirmDialogFragment.Builder addMessageArgument(
                @Nullable Object obj) {
            if (myMsgArgs == null) {
                myMsgArgs = new ArrayList<>();
            }

            myMsgArgs.add(obj);
            return this;
        }

        /**
         * Sets the affermative action text.
         *
         * @param  value The value as a string resource ID.
         * @return       This object.
         */
        @NonNull
        public ConfirmDialogFragment.Builder affirmativeActionText(
                @StringRes int value) {
            myDialogArgs.putInt(ConfirmDialogFragment.PROP_POSITIVEID, value);
            return this;
        }

        /**
         * Sets the dismissive action text.
         *
         * @param  value The value as a string resource ID.
         * @return       This object.
         */
        @NonNull
        public ConfirmDialogFragment.Builder dismissiveActionText(
                @StringRes int value) {
            myDialogArgs.putInt(ConfirmDialogFragment.PROP_NEGATIVEID, value);
            return this;
        }

        /**
         * Sets the event to post when the dialog has been closed.
         *
         * @param  obj The object. May be {@code null}.
         * @return     This object.
         * @see        <a href="http://greenrobot.org/eventbus"
         *             target="_top">EventBus: Events for Android</a>
         */
        @NonNull
        public ConfirmDialogFragment.Builder closeEvent(
                @Nullable DialogCloseEvent obj) {
            if (obj == null) {
                myDialogArgs.remove(ConfirmDialogFragment.PROP_CLOSEEVENT);
            } else {
                myDialogArgs.putParcelable(
                        ConfirmDialogFragment.PROP_CLOSEEVENT, obj);
            }

            return this;
        }

        /**
         * Shows a new {@code ConfirmDialogFragment} dialog.
         */
        public void show() {
            DialogFragment dlg;

            dlg = ConfirmDialogFragment.newInstance(this);
            dlg.show(myActivity.getSupportFragmentManager(),
                    ConfirmDialogFragment.TAG);
        }
    }
}
