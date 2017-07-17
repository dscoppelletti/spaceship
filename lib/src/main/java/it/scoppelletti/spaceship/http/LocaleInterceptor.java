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

package it.scoppelletti.spaceship.http;

import java.io.IOException;
import java.util.Locale;
import java.util.StringTokenizer;
import android.os.Build;
import android.text.TextUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import it.scoppelletti.spaceship.types.StringExt;

/**
 * Sets the {@code Accept-Language} header in HTTP requests.
 *
 * @since 1.0.0
 */
public final class LocaleInterceptor implements Interceptor {
    private static final String LANG_UND = "und";
    private static final String LOCALE_SEP = "_";
    private static final char TAG_SEP = '-';

    /**
     * Sole constructor.
     */
    public LocaleInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request newReq, originalReq;


        originalReq = chain.request();
        newReq = originalReq.newBuilder()
                .header(HttpExt.HEADER_LOCALE, toLanguageTag(
                        Locale.getDefault()))
                .build();

        return chain.proceed(newReq);
    }

    /**
     * Returns the language tag corresponding to a locale object.
     *
     * @param  locale The locale object.
     * @return        The language tag.
     */
    private String toLanguageTag(Locale locale) {
        String s;
        StringBuilder buf;
        StringTokenizer tokens;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return locale.toLanguageTag();
        }

        // Simple alternative implementation that does not strictly comply IEFT
        // BCP 47

        buf = new StringBuilder();
        s = locale.getLanguage();
        if (TextUtils.isEmpty(s)) {
            buf.append(LocaleInterceptor.LANG_UND);
        } else {
            buf.append(s.toLowerCase());
        }

        s = locale.getScript();
        if (!TextUtils.isEmpty(s)) {
            if (s.length() > 0) {
                buf.append(LocaleInterceptor.TAG_SEP);
            }

            buf.append(StringExt.toTitleCase(s));
        }

        s = locale.getCountry();
        if (!TextUtils.isEmpty(s)) {
            if (s.length() > 0) {
                buf.append(LocaleInterceptor.TAG_SEP);
            }

            buf.append(s.toUpperCase());
        }

        s = locale.getVariant();
        if (!TextUtils.isEmpty(s)) {
            tokens = new StringTokenizer(s, LocaleInterceptor.LOCALE_SEP);
            while (tokens.hasMoreTokens()) {
                if (s.length() > 0) {
                    buf.append(LocaleInterceptor.TAG_SEP);
                }

                buf.append(s.toLowerCase());
            }
        }

        return buf.toString();
    }
}
