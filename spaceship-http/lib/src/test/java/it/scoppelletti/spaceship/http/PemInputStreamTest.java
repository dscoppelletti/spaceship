package it.scoppelletti.spaceship.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public class PemInputStreamTest {
    private static final int BUFSIZE = 128;
    private static final String TEST1 = "-----BEGIN PRIVATE KEY-----\n" +
            "aaaaaa\n" +
            "bbbbbb\n" +
            "cccccc\n" +
            "-----END PRIVATE KEY-----\n";
    private static final String RES1 = "\naaaaaa\n" +
            "bbbbbb\n" +
            "cccccc\n\n";
    private static final String TEST2 = "----- xx -- xx -----\n" +
            "aaaaaa\n" +
            "bbbbbb\n" +
            "----- xx - xx -----\n" +
            "cccccc";
    private static final String RES2 = "\naaaaaa\n" +
            "bbbbbb\n\n" +
            "cccccc";
    private static final String TEST3 = "----------\n" +
            "aaa-aaa\n" +
            "--bbbbbb\n" +
            "cccccc\n" +
            "--";
    private static final String RES3 = "\naaa-aaa\n" +
            "--bbbbbb\n" +
            "cccccc\n" +
            "--";

    @Test
    public void test() throws Exception {
        String res;

        res = doTest(PemInputStreamTest.TEST1);
        MatcherAssert.assertThat(res, Matchers.equalTo(
                PemInputStreamTest.RES1));
        res = doTest(PemInputStreamTest.TEST2);
        MatcherAssert.assertThat(res, Matchers.equalTo(
                PemInputStreamTest.RES2));
        res = doTest(PemInputStreamTest.TEST3);
        MatcherAssert.assertThat(res, Matchers.equalTo(
                PemInputStreamTest.RES3));
    }

    private String doTest(String s) throws Exception {
        int n;
        InputStream in;
        InputStream pem;
        ByteArrayOutputStream out;
        byte[] buf;

        buf = new byte[PemInputStreamTest.BUFSIZE];
        in = new ByteArrayInputStream(s.getBytes());
        pem = new PemInputStream(in);
        out = new ByteArrayOutputStream();

        n = pem.read(buf, 0, PemInputStreamTest.BUFSIZE);
        while (n > 0) {
            out.write(buf, 0, n);
            n = pem.read(buf, 0, PemInputStreamTest.BUFSIZE);
        }

        out.flush();
        return new String(out.toByteArray());
    }
}
