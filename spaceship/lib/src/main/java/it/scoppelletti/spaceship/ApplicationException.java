/*
 * Copyright (C) 2008-2015 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;

/**
 * Application exception.
 *
 * @since 1.0.0
 */
public final class ApplicationException extends RuntimeException {
    private static final long serialVersionUID = 1;

    /**
     * @serial The message as a string resource ID.
     */
    private int myMsgId;

    /**
     * @serial The title as a string resource ID.
     */
    private int myTitleId;

    private transient Object[] myArgs;

    /**
     * Constructor.
     *
     * @param builder The instance builder.
     */
    private ApplicationException(ApplicationException.Builder builder) {
        super(builder.myCause);

        myMsgId = builder.myMsgId;
        myTitleId = builder.myTitleId;
        myArgs = (builder.myArgs == null) ? null : builder.myArgs.toArray();
    }

    /**
     * Serializes this object.
     *
     * @serialData Serialized fields followed by:
     *
     * <ol>
     * <li>The length of the argument array ({@code int}).</li>
     * <li>All elements of the argument array (each element an {@code Object})
     * in the proper order.</li>
     * </ol>
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        int i, n;
        Object obj;

        out.defaultWriteObject();

        n = (myArgs == null) ? 0 : myArgs.length;
        out.writeInt(n);
        for (i = 0; i < n; i++) {
            obj = myArgs[i];
            if (obj instanceof Serializable) {
                out.writeObject(obj);
            } else if (obj == null) {
                out.writeObject(null);
            } else {
                out.writeObject(obj.toString());
            }
        }
    }

    /**
     * Deserializes this object.
     */
    private void readObject(ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        int i, n;

        in.defaultReadObject();

        n = in.readInt();
        if (n <= 0) {
            myArgs = null;
        } else {
            myArgs = new Object[n];
            for (i = 0; i < n; i++) {
                myArgs[i] = in.readObject();
            }
        }
    }

    /**
     * Gets the message as a string resource ID.
     *
     * @return The value.
     */
    @StringRes
    public int getMessageId() {
        return myMsgId;
    }

    /**
     * Gets the arguments.
     *
     * @return The array. May be {@code null}.
     */
    @Nullable
    public Object[] getMessageArguments() {
        return myArgs;
    }

    /**
     * Gets the title as a string resource ID.
     *
     * @return The value.
     */
    @StringRes
    public int getTitleId() {
        return myTitleId;
    }

    @Override
    public String getMessage() {
        return toString();
    }

    @Override
    public String toString() {
        int i, n;
        StringBuilder buf;

        buf = new StringBuilder(getClass().getName())
                .append("(messageId=")
                .append(myMsgId)
                .append(";arguments=[");

        n = (myArgs == null) ? 0 : myArgs.length;
        for (i = 0; i < n; i++) {
            if (i > 0) {
                buf.append(", ");
            }

            buf.append(myArgs[i]);
        }

        return buf.append("];titleId=")
                .append(myTitleId)
                .append(")").toString();
    }

    /**
     * Returns a string representation of an exception.
     *
     * <p>The string representation is obtained by the following fallbacks:</p>
     *
     * <ol>
     * <li>The {@code getLocalizedMessage} method of the exception object.</li>
     * <li>The {@code getMessage} method of the exception object.</li>
     * <li>The {@code toString} method of the exception object.</li>
     * <li>Fully qualified name of the exception class.</li>
     * </ol>
     *
     * @param  ex The exception.
     * @return    The string. If {@code ex} is {@code null}, returns the string
     *            {@code "null"}.
     */
    @NonNull
    public static String toString(@Nullable Throwable ex) {
        String msg;

        if (ex == null) {
            return "null";
        }

        if (ex instanceof InvocationTargetException &&
                ex.getCause() != null) {
            ex = ex.getCause();
        }

        msg = ApplicationException.getMessage(ex);

        if (ex instanceof ClassNotFoundException) {
            // The original message is merely the class name
            msg = String.format("Class %1$s not found.", msg);
        }

        return msg;
    }

    /**
     * Returns a string representation of an exception.
     *
     * @param  ex The exception.
     * @return    The string.
     */
    private static String getMessage(Throwable ex) {
        String s;

        s = ex.getLocalizedMessage();
        if (!TextUtils.isEmpty(s)) {
            return s;
        }

        s = ex.getMessage();
        if (!TextUtils.isEmpty(s)) {
            return s;
        }

        s = ex.toString();
        if (!TextUtils.isEmpty(s)) {
            return s;
        }

        return ex.getClass().getCanonicalName();
    }

    /**
     * Builds an {@code ApplicationException} instance.
     *
     * @since 1.0.0
     */
    public static final class Builder {
        private final int myMsgId;
        private int myTitleId;
        private Throwable myCause;
        private List<Object> myArgs;

        /**
         * Constructor.
         *
         * @param msgId The message as a string resource ID.
         */
        public Builder(@StringRes int msgId) {
            myMsgId = msgId;
            myTitleId = android.R.string.dialog_alert_title;
        }

        /**
         * Sets the message arguments.
         *
         * @param  v The array of arguments.
         * @return   This object.
         */
        @NonNull
        public ApplicationException.Builder messageArguments(Object... v) {
            myArgs = (v == null) ? null : Arrays.asList(v);
            return this;
        }

        /**
         * Adds a message argument.
         *
         * @param  obj The object. May be {@code null}.
         * @return     This object.
         */
        @NonNull
        public ApplicationException.Builder addMessageArgument(
                @Nullable Object obj) {
            if (myArgs == null) {
                myArgs = new ArrayList<>();
            }

            myArgs.add(obj);
            return this;
        }

        /**
         * Sets the title.
         *
         * @param  value The value as a string resource ID.
         * @return       This object.
         */
        @NonNull
        public ApplicationException.Builder title(@StringRes int value) {
            myTitleId = value;
            return this;
        }

        /**
         * Sets the cause.
         *
         * @param  ex The object. May be {@code null}.
         * @return    This object.
         */
        @NonNull
        public ApplicationException.Builder cause(@Nullable Throwable ex) {
            myCause = ex;
            return this;
        }

        /**
         * Builds a new {@code ApplicationException} instance.
         *
         * @return The new object.
         */
        @NonNull
        public ApplicationException build() {
            return new ApplicationException(this);
        }
    }
}
