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
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import it.scoppelletti.spaceship.ApplicationException;

/**
 * Decorates an HTTP request with infos describing the client.
 *
 * @since 1.0.0
 */
public final class ClientInterceptor implements Interceptor {
    private static final char LANG_SEP = '-';
    private static final char VALUE_SEP = ';';
    private static final String LANG_UND = "und";
    private static final String LOCALE_SEP = "_";
    private static final String OS_NAME = "android";
    private final Context myCtx;

    /**
     * Constructor.
     *
     * @param ctx The context.
     */
    public ClientInterceptor(@NonNull Context ctx) {
        if (ctx == null) {
            throw new NullPointerException("Argument ctx is null.");
        }

        myCtx = ctx.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request newReq, originalReq;

        originalReq = chain.request();
        newReq = originalReq.newBuilder()
                .header(HttpExt.HEADER_OS, getOS())
                .header(HttpExt.HEADER_APPL, getAppl())
                .header(HttpExt.HEADER_LOCALE, toLanguageTag(
                        Locale.getDefault()))
                .build();

        return chain.proceed(newReq);
    }

    /**
     * Returns the OS name and version.
     *
     * @return The value.
     */
    private String getOS() {
        return new StringBuilder(ClientInterceptor.OS_NAME)
                .append(ClientInterceptor.VALUE_SEP)
                .append(Build.VERSION.SDK_INT).toString();
    }

    /**
     * Returns the application name and version.
     *
     * @return The value.
     */
    private String getAppl() {
        String name;
        PackageInfo packageInfo;
        PackageManager packageMgr;

        name = myCtx.getPackageName();
        packageMgr = myCtx.getPackageManager();
        try {
            packageInfo = packageMgr.getPackageInfo(name, 0);
        } catch (PackageManager.NameNotFoundException ex) {
            throw new ApplicationException.Builder(
                    R.string.it_scoppelletti_err_packageNotFound)
                    .messageArguments(name)
                    .cause(ex).build();
        }

        return new StringBuilder(name)
                .append(ClientInterceptor.VALUE_SEP)
                .append(packageInfo.versionCode).toString();
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
            buf.append(ClientInterceptor.LANG_UND);
        } else {
            buf.append(s.toLowerCase());
        }

        s = locale.getCountry();
        if (!TextUtils.isEmpty(s)) {
            if (s.length() > 0) {
                buf.append(ClientInterceptor.LANG_SEP);
            }

            buf.append(s.toUpperCase());
        }

        s = locale.getVariant();
        if (!TextUtils.isEmpty(s)) {
            tokens = new StringTokenizer(s, ClientInterceptor.LOCALE_SEP);
            while (tokens.hasMoreTokens()) {
                if (s.length() > 0) {
                    buf.append(ClientInterceptor.LANG_SEP);
                }

                buf.append(s.toLowerCase());
            }
        }

        return buf.toString();
    }
}
