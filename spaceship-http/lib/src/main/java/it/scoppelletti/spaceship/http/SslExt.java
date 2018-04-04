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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Base64InputStream;
import it.scoppelletti.spaceship.ApplicationException;
import it.scoppelletti.spaceship.io.IOExt;
import it.scoppelletti.spaceship.security.SecurityExt;

/**
 * Operations on <abbr title="Secure Sockets Layer">SSL</abbr>.
 *
 * @since 1.0.0
 */
public final class SslExt {

    /**
     * Type of certificates.
     */
    public static final String CERT_TYPE = "X.509";

    /**
     * Algorithm for key pairs.
     */
    public static final String KEY_ALG = "RSA";

    /**
     * Algorithm for {@code KeyManager} and {@code TrustManager}.
     */
    public static final String KEYMANAGER_ALG = "PKIX";

    /**
     * Keystore type.
     */
    public static final String KEYSTORE_TYPE = "PKCS12";

    /**
     * SSL protocol.
     */
    public static final String SSL_PROTOCOL = "TLS";

    private static final int BUFSIZE = 128;
    private static final int PASSWORD_LEN = 16;
    private static final String ALIAS_PRIVATEKEY = "private";

    /**
     * Private constructor for static class.
     */
    private SslExt() {
    }

    /**
     * Creates a new {@code SSLContext} instance.
     *
     * @param  km Manager of key material that is used as authentication
     *            credentials to present to the server. May be {@code null}.
     * @param  tm Manager of the trust material that is used for deciding
     *            whether credentials presented by a server should be accepted.
     *            May be {@code null}.
     * @return    The new object.
     */
    @NonNull
    public static SSLContext newContext(@Nullable KeyManager km,
            @Nullable TrustManager tm) {
        SSLContext ctx;
        KeyManager[] kms;
        TrustManager[] tms;

        kms = (km == null) ? null : new KeyManager[] { km };
        tms = (tm == null) ? null : new TrustManager[] { tm };

        try {
            ctx = SSLContext.getInstance(SslExt.SSL_PROTOCOL);
            ctx.init(kms, tms, null);
        } catch (GeneralSecurityException ex) {
            throw new ApplicationException.Builder(
                    R.string.it_scoppelletti_err_security)
                    .cause(ex).build();
        }

        return ctx;
    }

    /**
     * Loads a certificate to use as authentication credentials to present to
     * the server.
     *
     * @param  keyIn  Input stream for reading the private key from a PEM file.
     * @param  certIn Input stream for reading the client certificate from a
     *                PEM file.
     * @return        The {@code KeyManager} object.
     */
    @NonNull
    public static X509KeyManager loadKeyManager(@NonNull InputStream keyIn,
            @NonNull InputStream certIn) {
        KeyStore ks;
        PrivateKey privateKey;
        KeyManagerFactory kmFactory;
        CertificateFactory certFactory;
        Collection<? extends Certificate> certs;
        char[] pwd = null;
        KeyManager[] kms;
        Certificate[] certArray;

        if (keyIn == null) {
            throw new NullPointerException("Argument keyIn is null.");
        }
        if (certIn == null) {
            throw new NullPointerException("Argument certIn is null.");
        }

        privateKey = loadPrivateKey(keyIn);

        try {
            certFactory = CertificateFactory.getInstance(SslExt.CERT_TYPE);
            certs = certFactory.generateCertificates(certIn);
            if (certs.isEmpty()) {
                throw new IllegalArgumentException(
                        "Set of client certificates is empty.");
            }

            certArray = certs.toArray(new Certificate[certs.size()]);

            ks = KeyStore.getInstance(SslExt.KEYSTORE_TYPE);
            pwd = SslExt.generatePassword(SslExt.PASSWORD_LEN);
            ks.load(null, pwd);

            ks.setKeyEntry(SslExt.ALIAS_PRIVATEKEY, privateKey, pwd, certArray);

            kmFactory = KeyManagerFactory.getInstance(SslExt.KEYMANAGER_ALG);
            kmFactory.init(ks, pwd);
        } catch (GeneralSecurityException|IOException ex) {
            throw new ApplicationException.Builder(
                    R.string.it_scoppelletti_err_security)
                    .cause(ex).build();
        } finally {
            if (pwd !=  null) {
                Arrays.fill(pwd, '\0');
            }
        }

        kms = kmFactory.getKeyManagers();
        if (kms.length != 1 || !(kms[0] instanceof X509KeyManager)) {
            throw new IllegalStateException(
                    "Unexpected default key managers.");
        }

        return (X509KeyManager) kms[0];
    }

    /**
     * Loads a private key from a PEM file.
     *
     * @param  in Input stream.
     * @return    The key.
     * @see       <a href="http://stackoverflow.com/questions/11787571"
     *            target="_blank">How to read .pem file to get private and
     *            public key</a>
     */
    private static PrivateKey loadPrivateKey(InputStream in) {
        int n;
        PrivateKey key;
        KeySpec keySpec;
        KeyFactory keyFactory;
        InputStream decoder, pemFilter;
        ByteArrayOutputStream out = null;
        byte[] buf = null;

        pemFilter = new PemInputStream(in);
        decoder = new Base64InputStream(pemFilter, Base64.DEFAULT);
        out = new ByteArrayOutputStream();
        buf = new byte[SslExt.BUFSIZE];

        try {
            n = decoder.read(buf, 0, SslExt.BUFSIZE);
            while (n > 0) {
                out.write(buf, 0, n);
                n = decoder.read(buf, 0, SslExt.BUFSIZE);
            }

            Arrays.fill(buf, (byte) 0);
            out.flush();
            buf = out.toByteArray();
        } catch (IOException ex) {
            throw new ApplicationException.Builder(
                    R.string.it_scoppelletti_err_security)
                    .cause(ex).build();
        } finally {
            out = IOExt.close(out);
        }

        try {
            keySpec = new PKCS8EncodedKeySpec(buf);
            keyFactory = KeyFactory.getInstance(SslExt.KEY_ALG);
            key = keyFactory.generatePrivate(keySpec);
        } catch (GeneralSecurityException ex) {
            throw new ApplicationException.Builder(
                    R.string.it_scoppelletti_err_security)
                    .cause(ex).build();
        } finally {
            Arrays.fill(buf, (byte) 0);
        }

        return key;
    }

    /**
     * Loads a certificate to use as a trusted authority for signing server
     * certificates.
     *
     * @param  in Input stream for reading the certificate from a PEM file.
     * @return    The {@code TrustManager} object.
     */
    @NonNull
    public static X509TrustManager loadTrustManager(@NonNull InputStream in) {
        int i;
        KeyStore ks;
        TrustManagerFactory tmFactory;
        CertificateFactory certFactory;
        Collection<? extends Certificate> certs;
        char[] pwd = null;
        TrustManager[] tms;

        if (in == null) {
            throw new NullPointerException("Argument in is null.");
        }

        try {
            certFactory = CertificateFactory.getInstance(SslExt.CERT_TYPE);
            certs = certFactory.generateCertificates(in);
            if (certs.isEmpty()) {
                throw new IllegalArgumentException(
                        "Set of trusted certificates is empty.");
            }

            ks = KeyStore.getInstance(SslExt.KEYSTORE_TYPE);
            pwd = SslExt.generatePassword(SslExt.PASSWORD_LEN);
            ks.load(null, pwd);

            i = 0;
            for (Certificate cert : certs) {
                ks.setCertificateEntry(Integer.toString(i), cert);
                i++;
            }

            tmFactory = TrustManagerFactory.getInstance(SslExt.KEYMANAGER_ALG);
            tmFactory.init(ks);
        } catch (GeneralSecurityException|IOException ex) {
            throw new ApplicationException.Builder(
                    R.string.it_scoppelletti_err_security)
                    .cause(ex).build();
        } finally {
            if (pwd !=  null) {
                Arrays.fill(pwd, '\0');
            }
        }

        tms = tmFactory.getTrustManagers();
        if (tms.length != 1 || !(tms[0] instanceof X509TrustManager)) {
            throw new IllegalStateException(
                    "Unexpected default trust managers.");
        }

        return (X509TrustManager) tms[0];
    }

    /**
     * Generates a password.
     *
     * @param  len Length of the password.
     * @return     The new password.
     */
    private static char[] generatePassword(int len) {
        int c, i, k;
        Random rnd;
        char[] v;

        rnd = SecurityExt.getCSRNG();
        v = new char[len];
        for (i = 0; i < len; i++) {
            k = rnd.nextInt(62);
            if (k < 10) {
                c = '0' + k;
            } else if (k < 36) {
                c = 'a' + k - 10;
            } else {
                c = 'A' + k - 36;
            }

            v[i] = (char) c;
        }

        return v;
    }
}
