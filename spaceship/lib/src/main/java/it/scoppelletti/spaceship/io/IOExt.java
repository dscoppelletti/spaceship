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

package it.scoppelletti.spaceship.io;

import java.io.Closeable;
import java.io.IOException;
import android.support.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;

/**
 * <abbr title="Input/Output">I/O</abbr> operations.
 *
 * @since 1.0.0
 */
@Slf4j
public final class IOExt {

    /**
     * Private constructor for static class.
     */
    private IOExt() {
    }

    /**
     * Closes a stream.
     *
     * <p>If throws an exception, this will be logged and ignored.<br />
     * The returned value is always {@code null} and can be assigned to the
     * {@code stream} variable, so that it can be used to check whether the
     * stream is still opened ({@code stream != null}) or has been closed
     * ({@code stream == null}).</p>
     *
     * @param  <T>    Class of the stream.
     * @param  stream A stream. If {@code null}, assumes that the stream has
     *                been already closed.
     * @return        {@code null}.
     */
    public static <T extends Closeable> T close(@Nullable T stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException|RuntimeException ex) {
                myLogger.error("Failed to close stream.", ex);
            }
        }

        return null;
    }
}
