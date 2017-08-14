package it.scoppelletti.spaceship.http.sample;

import java.io.InputStream;
import java.util.concurrent.Callable;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import it.scoppelletti.spaceship.http.ClientInterceptor;
import it.scoppelletti.spaceship.http.HttpExt;
import it.scoppelletti.spaceship.http.SslExt;
import it.scoppelletti.spaceship.io.IOExt;

final class GreetingCallable implements Callable<String> {
    private final Context myCtx;

    GreetingCallable(@NonNull Context ctx) {
        if (ctx == null) {
            throw new NullPointerException("Argument ctx is null.");
        }

        myCtx = ctx.getApplicationContext();
    }

    @Override
    public String call() throws Exception {
        GreetingService client;
        Call<String> req;
        Response<String> resp;

        client = createClient();
        req = client.getPublicGreeting();
        resp = HttpExt.execute(req);
        return resp.body();
    }

    private GreetingService createClient() throws Exception {
        InputStream certIn = null;
        InputStream keyIn = null;
        Retrofit retrofit;
        OkHttpClient httpClient;
        AssetManager assetMgr;
        SSLContext sslCtx;
        X509KeyManager km;
        X509TrustManager tm;

        assetMgr = myCtx.getAssets();
        try {
            keyIn = assetMgr.open("clientkey.pem");
            certIn = assetMgr.open("client.pem");
            km = SslExt.loadKeyManager(keyIn, certIn);
        } finally {
            keyIn = IOExt.close(keyIn);
            certIn = IOExt.close(certIn);
        }

        try {
            certIn = assetMgr.open("ca.pem");
            tm = SslExt.loadTrustManager(certIn);
        } finally {
            certIn = IOExt.close(certIn);
        }

        sslCtx = SslExt.newContext(km, tm);

        httpClient = new OkHttpClient.Builder()
                .addInterceptor(new ClientInterceptor(myCtx))
                .sslSocketFactory(sslCtx.getSocketFactory(), tm)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVICE_URL)
                .client(httpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        return retrofit.create(GreetingService.class);
    }
}
