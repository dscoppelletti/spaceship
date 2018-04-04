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

package it.scoppelletti.spaceship.types;

import android.support.annotation.Nullable;

/**
 * Operations on strings.
 *
 * @since 1.0.0
 */
public final class StringExt {

    /**
     * Empty string.
     */
    public static final String EMPTY = "";

    /**
     * Private constructor for static class.
     */
    private StringExt() {
    }

    /**
     * Converts a string changing the first character of each word to the
     * corresponding title-case character.
     *
     * @param  s The original string. May be {@code null}.
     * @return   The converted string. May be {@code null}.
     */
    @Nullable
    public static CharSequence toTitleCase(@Nullable CharSequence s) {
        int i, n;
        char c;
        boolean cap;
        StringBuilder buf;

        if (s == null) {
            return null;
        }

        cap = true;
        n = s.length();
        buf = new StringBuilder(n);
        for (i = 0; i < n; i++) {
            c = s.charAt(i);
            if (Character.isSpaceChar(c)) {
                cap = true;
                buf.append(c);
            } else if (cap) {
                cap = false;
                buf.append(Character.toTitleCase(c));
            } else {
                buf.append(c);
            }
        }

        return buf.toString();
    }
}
