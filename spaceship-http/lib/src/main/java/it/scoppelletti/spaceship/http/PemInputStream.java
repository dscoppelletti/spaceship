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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StreamCorruptedException;
import java.util.Locale;
import android.support.annotation.NonNull;

/**
 * PEM input stream filter.
 *
 * <p>This filter strips the sequences like {@code "----- ... -----"}.</p>
 */
final class PemInputStream extends FilterInputStream {
    private static final int CHAR_DASH = '-';
    private static final int CHAR_EOF = -1;
    private static final int CHAR_NA = -2;
    private static final int STATUS_BODY = 0;
    private static final int STATUS_BUFFER = 1;
    private static final int STATUS_COMMENT = 10;
    private static final int STATUS_TAGOPEN = 11;
    private static final int STATUS_TAGCLOSE = 12;
    private static final int TAG_LEN = 5;
    private int myStatus;
    private int myTagLen;
    private int myBuffer;

    /**
     * Constructor.
     *
     * @param in Input stream to filter.
     */
    PemInputStream(@NonNull InputStream in) {
        super(in);
        myStatus = PemInputStream.STATUS_BODY;
        myTagLen = 0;
        myBuffer = PemInputStream.CHAR_NA;
    }

    @Override
    public int read() throws IOException {
        int c;

        c = readChar();
        while (c != PemInputStream.CHAR_EOF) {
            switch (myStatus) {
            case PemInputStream.STATUS_BODY:
                if (c != PemInputStream.CHAR_DASH) {
                    return c;
                }

                // Begin of opening tag
                myStatus = PemInputStream.STATUS_TAGOPEN;
                myTagLen = 1;
                break;

            case PemInputStream.STATUS_BUFFER:
                return c;

            case PemInputStream.STATUS_TAGOPEN:
                if (c == PemInputStream.CHAR_DASH) {
                    // Opening tag continue
                    myTagLen++;
                    if (myTagLen == PemInputStream.TAG_LEN) {
                        // End of opening tag
                        myStatus = PemInputStream.STATUS_COMMENT;
                        myTagLen = 0;
                    }
                } else {
                    // Opening tag interupted
                    myStatus = PemInputStream.STATUS_BUFFER;
                    myBuffer = c;
                }
                break;

            case PemInputStream.STATUS_COMMENT:
                if (c == PemInputStream.CHAR_DASH) {
                    // Begin of closing tag
                    myStatus = PemInputStream.STATUS_TAGCLOSE;
                    myTagLen = 1;
                }
                break;

            case PemInputStream.STATUS_TAGCLOSE:
                if (c == PemInputStream.CHAR_DASH) {
                    // Closing tag continue
                    myTagLen++;
                    if (myTagLen == PemInputStream.TAG_LEN) {
                        // End of closing tag
                        myStatus = PemInputStream.STATUS_BODY;
                        myTagLen = 0;
                    }
                } else {
                    // Closing tag interrupted
                    myStatus = PemInputStream.STATUS_COMMENT;
                }
                break;

            default:
                throw new StreamCorruptedException("Unexpected status.");
            }

            c = readChar();
        }

        switch (myStatus) {
        case PemInputStream.STATUS_TAGOPEN:
            // Opening tag interupted
            myStatus = PemInputStream.STATUS_BUFFER;
            myBuffer = PemInputStream.CHAR_EOF;
            return readChar();
        }

        return PemInputStream.CHAR_EOF;
    }

    /**
     * Reads the next character from the original stream.
     *
     * @return The read character.
     */
    private int readChar() throws IOException {
        int c;

        switch (myStatus) {
        case PemInputStream.STATUS_BUFFER:
            if (myTagLen > 0) {
                myTagLen--;
                return PemInputStream.CHAR_DASH;
            }
            if (myBuffer != PemInputStream.CHAR_NA) {
                c = myBuffer;
                myBuffer = PemInputStream.CHAR_NA;
                return c;
            }

            myStatus = PemInputStream.STATUS_BODY;
            break;
        }

        return this.in.read();
    }

    @Override
    public int read(@NonNull byte[] b, int off, int len) throws IOException {
        int c, n;

        if (b == null) {
            throw new NullPointerException("Argument b is null.");
        }
        if (off < 0) {
            throw new IndexOutOfBoundsException("Argument off < 0.");
        }
        if (len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException(String.format(
                    Locale.ENGLISH, "Argument len not in [0, %1$d].",
                    b.length - off));
        }
        if (len == 0) {
            return 0;
        }

        n = 0;
        c = read();
        while (c != PemInputStream.CHAR_EOF && n < len) {
            b[off + n] = (byte) c;
            n++;
            if (n == len) {
                break;
            }

            c = read();
        }

        return n;
    }

    @Override
    public long skip(long n) throws IOException {
        throw new IOException("Skip not supported.");
    }

    @Override
    public boolean markSupported() {
        return false;
    }
}
