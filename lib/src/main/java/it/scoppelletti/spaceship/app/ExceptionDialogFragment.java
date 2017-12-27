/*
 * Copyright (C) 2015 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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
import org.greenrobot.eventbus.EventBus;
import it.scoppelletti.spaceship.ApplicationException;
import it.scoppelletti.spaceship.ExceptionEvent;

/**
 * Exception dialog.
 *
 * @since 1.0.0
 */
@UiThread
public final class ExceptionDialogFragment extends AppCompatDialogFragment {

    /**
     * The fragment tag.
     */
    public static final String TAG = AppExt.TAG_EXCEPTIONDIALOG;

    private static final String PROP_TITLEID = "1";
    private static final String PROP_MSG = "2";
    private static final String PROP_MSGID = "3";
    private static final String PROP_EX = "4";
    private static final String PROP_CLOSEEVENT = "5";

    /**
     * Sole constructor.
     */
    public ExceptionDialogFragment() {
    }

    /**
     * Creates a new fragment instance.
     *
     * @param  builder The instance builder.
     * @return         The new object.
     */
    private static ExceptionDialogFragment newInstance(
            ExceptionDialogFragment.Builder builder) {
        ExceptionDialogFragment fragment;

        if (builder.myMsgId > 0) {
            if (builder.myMsgArgs == null) {
                builder.myDialogArgs.remove(ExceptionDialogFragment.PROP_MSG);
                builder.myDialogArgs.putInt(ExceptionDialogFragment.PROP_MSGID,
                        builder.myMsgId);
            } else {
                builder.myDialogArgs.remove(ExceptionDialogFragment.PROP_MSGID);
                builder.myDialogArgs.putString(ExceptionDialogFragment.PROP_MSG,
                        builder.myActivity.getString(builder.myMsgId,
                                builder.myMsgArgs.toArray()));
            }
        }

        fragment = new ExceptionDialogFragment();
        fragment.setArguments(builder.myDialogArgs);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        int titleId;
        Bundle args;
        Throwable ex;
        Activity activity;
        ApplicationException applEx;
        AlertDialog.Builder builder;

        activity = getActivity();
        args = getArguments();
        ex = (Throwable) args.getSerializable(ExceptionDialogFragment.PROP_EX);
        applEx = (ex instanceof ApplicationException) ?
                (ApplicationException) ex : null;
        titleId = args.getInt(ExceptionDialogFragment.PROP_TITLEID,
                (applEx == null) ? android.R.string.dialog_alert_title :
                applEx.getTitleId());

        builder = new AlertDialog.Builder(activity)
                .setTitle(titleId)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setNegativeButton(android.R.string.cancel, (dialog, which) ->
                        post());

        setMessage(activity, builder, args, ex);

        return builder.create();
    }

    /**
     * Sets the message.
     *
     * @param activity The activity.
     * @param builder  The dialog builder.
     * @param args     The fragment arguments.
     * @param ex       The exception. May be {@code null}.
     */
    private void setMessage(Activity activity, AlertDialog.Builder builder,
            Bundle args, Throwable ex) {
        int msgId;
        String msg;
        ApplicationException applEx;

        msgId = args.getInt(ExceptionDialogFragment.PROP_MSGID, -1);
        if (msgId > 0) {
            builder.setMessage(msgId);
            return;
        }

        msg = args.getString(ExceptionDialogFragment.PROP_MSG);
        if (!TextUtils.isEmpty(msg)) {
            builder.setMessage(msg);
            return;
        }

        if (ex instanceof ApplicationException) {
            applEx = (ApplicationException) ex;
            if (applEx.getMessageArguments() == null) {
                builder.setMessage(applEx.getMessageId());
            } else {
                builder.setMessage(activity.getString(applEx.getMessageId(),
                        applEx.getMessageArguments()));
            }
        } else if (ex != null) {
            builder.setMessage(ApplicationException.toString(ex));
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        post();
    }

    /**
     * Posts the dialog result.
     */
    private void post() {
        Bundle args;
        DialogCloseEvent req;

        args = getArguments();
        req = args.getParcelable(ExceptionDialogFragment.PROP_CLOSEEVENT);
        if (req != null) {
            req.setResult(DialogInterface.BUTTON_NEGATIVE);
            EventBus.getDefault().post(req);
        }
    }

    /**
     * Builds an {@code ExceptionDialogFragment} instance.
     *
     * @since 1.0.0
     */
    public static final class Builder {
        private final FragmentActivity myActivity;
        private final Bundle myDialogArgs;
        private int myMsgId;
        private List<Object> myMsgArgs;
        private int myReqCode;

        /**
         * Constructor.
         *
         * @param activity The activity.
         */
        public Builder(@NonNull Activity activity) {
            if (activity == null) {
                throw new NullPointerException("Argument activity is null.");
            }

            myActivity = (FragmentActivity) activity;
            myDialogArgs = new Bundle();
        }

        /**
         * Sets the title.
         *
         * @param  value The value as a string resource ID.
         * @return       This object.
         */
        @NonNull
        public ExceptionDialogFragment.Builder title(@StringRes int value) {
            myDialogArgs.putInt(ExceptionDialogFragment.PROP_TITLEID, value);
            return this;
        }

        /**
         * Sets the message.
         *
         * @param  value The value as a string resource ID.
         * @return       This object.
         */
        @NonNull
        public ExceptionDialogFragment.Builder message(@StringRes int value) {
            myMsgId = value;
            return this;
        }

        /**
         * Sets the message arguments.
         *
         * @param  v The array of arguments.
         * @return   This object.
         */
        @NonNull
        public ExceptionDialogFragment.Builder messageArguments(Object... v) {
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
        public ExceptionDialogFragment.Builder addMessageArgument(
                @Nullable Object obj) {
            if (myMsgArgs == null) {
                myMsgArgs = new ArrayList<>();
            }

            myMsgArgs.add(obj);
            return this;
        }

        /**
         * Sets the exception.
         *
         * @param  ex The object. May be {@code null}.
         * @return    This object.
         */
        @NonNull
        public ExceptionDialogFragment.Builder throwable(
                @Nullable Throwable ex) {
            if (ex == null) {
                myDialogArgs.remove(ExceptionDialogFragment.PROP_EX);
            } else {
                myDialogArgs.putSerializable(ExceptionDialogFragment.PROP_EX,
                        ex);
            }

            return this;
        }

        /**
         * Sets the event notifiying the exception.
         *
         * @param  obj The object. May be {@code null}.
         * @return     This object.
         */
        public ExceptionDialogFragment.Builder exceptionEvent(
                @Nullable ExceptionEvent obj) {
            if (obj == null) {
                return this;
            }

            throwable(obj.getThrowable());
            if (obj.getTitleId() > 0) {
                title(obj.getTitleId());
            }
            if (obj.getRequestCode() != 0) {
                closeEvent(new DialogCloseEvent(obj.getRequestCode()));
            }

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
        public ExceptionDialogFragment.Builder closeEvent(
                @Nullable DialogCloseEvent obj) {
            if (obj == null) {
                myDialogArgs.remove(ExceptionDialogFragment.PROP_CLOSEEVENT);
            } else {
                myDialogArgs.putParcelable(
                        ExceptionDialogFragment.PROP_CLOSEEVENT, obj);
            }

            return this;
        }

        /**
         * Shows a new {@code ExceptionDialogFragment} dialog.
         */
        public void show() {
            DialogFragment dlg;

            dlg = ExceptionDialogFragment.newInstance(this);
            dlg.show(myActivity.getSupportFragmentManager(),
                    ExceptionDialogFragment.TAG);
        }
    }
}
