/*
 * Copyright (C) 2010-2017 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship.security;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.GetChars;
import android.text.TextUtils;

/**
 * Secure string.
 *
 * <p>Strings are immutable objects that remain in memory until the GC discards
 * them, so they are not safe to keep sensitive data such as passwords or secret
 * keys. A {@code SecureString} object keeps a string as a character array, so
 * you can explicitly clean the string when you do not use it anymore.</p>
 *
 * @since 1.0.0
 */
public final class SecureString implements GetChars {
    private char[] myValue;

    /**
     * Constructor.
     *
     * @param value The source string. May be {@code null}.
     */
    public SecureString(@Nullable String value) {
        if (TextUtils.isEmpty(value)) {
            myValue = new char[0];
        } else {
            myValue = value.toCharArray();
        }
    }

    /**
     * Constructor.
     *
     * @param value The source string. May be {@code null}.
     */
    public SecureString(@Nullable CharSequence value) {
        int i, n;

        if (TextUtils.isEmpty(value)) {
            myValue = new char[0];
        } else {
            n = value.length();
            myValue = new char[n];
            for (i = 0; i < n; i++) {
                myValue[i] = value.charAt(i);
            }
        }
    }

    /**
     * Constructor.
     *
     * @param value The source char array. May be {@code null}.
     */
    public SecureString(@Nullable final char[] value) {
        int n;

        n = (value == null) ? 0 : value.length;
        if (n == 0) {
            myValue = new char[0];
        } else {
            myValue = new char[n];
            System.arraycopy(value, 0, myValue, 0, n);
        }
    }

    /**
     * Constructor.
     *
     * @param value The source byte array. May be {@code null}.
     */
    public SecureString(@Nullable final byte[] value) {
        ByteBuffer byteBuf;
        CharBuffer charBuf;
        char[] cleaner;

        if (value == null || value.length == 0) {
            myValue = new char[0];
        } else {
            byteBuf = ByteBuffer.wrap(value);
            charBuf = Charset.defaultCharset().decode(byteBuf);

            myValue = new char[charBuf.limit()];
            charBuf.get(myValue);

            charBuf.clear();
            cleaner = new char[charBuf.capacity()];
            Arrays.fill(cleaner, '\0');
            charBuf.put(cleaner);
        }
    }

    /**
     * Clears the value.
     */
    public void clear() {
        Arrays.fill(myValue, '\0');
        myValue = new char[0];
    }

    @Override
    public int length() {
        return myValue.length;
    }

    @Override
    public char charAt(int index) {
        return myValue[index];
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return new String(myValue, start, end - start);
    }

    @Override
    public void getChars(int start, int end, char[] dest, int destoff) {
        int i;

        for (i = start; i < end; i++) {
            dest[destoff + i - start] = myValue[start];
        }
    }

    /**
     * Converts this object to a byte array.
     *
     * @return The resulting array.
     */
    @NonNull
    public byte[] toByteArray() {
        ByteBuffer byteBuf;
        CharBuffer charBuf;
        byte[] cleaner, v;

        charBuf = CharBuffer.wrap(myValue);
        byteBuf = Charset.defaultCharset().encode(charBuf);

        v = new byte[byteBuf.limit()];
        byteBuf.get(v);

        byteBuf.clear();
        cleaner = new byte[byteBuf.capacity()];
        Arrays.fill(cleaner, (byte) 0);
        byteBuf.put(cleaner);

        return v;
    }

    @NonNull
    @Override
    public String toString() {
        return new String(myValue);
    }

    @Override
    public boolean equals(Object obj) {
        int i, n;
        SecureString op;

        if (!(obj instanceof SecureString)) {
            return false;
        }

        op = (SecureString) obj;
        n = myValue.length;
        if (n != op.length()) {
            return false;
        }

        for (i = 0; i < n; i++) {
            if (myValue[i] != op.charAt(i)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int value = 17;

        for (Character c : myValue) {
            value = 37 * value + c.hashCode();
        }

        return value;
    }
}
