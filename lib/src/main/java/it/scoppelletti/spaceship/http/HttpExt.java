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

/**
 * Operations for HTTP protocol.
 *
 * @since 1.0.0
 */
public final class HttpExt {

    /**
     * Header reporting the client application name and version.
     */
    public static final String HEADER_APPL = "X-scoppelletti-appl";

    /**
     * Header {@code Authorization}.
     */
    public static final String HEADER_AUTH = "Authorization";

    /**
     * Header {@code Accept-Language}.
     */
    public static final String HEADER_LOCALE = "Accept-Language";

    /**
     * Header reporting the client <abbr title="Operative Syteme">OS</abbr> name
     * and version.
     */
    public static final String HEADER_OS = "X-scoppelletti-os";

    /**
     * Private constructor for static class.
     */
    private HttpExt() {
    }
}
