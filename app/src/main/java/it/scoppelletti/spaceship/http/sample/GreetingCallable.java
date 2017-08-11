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
        InputStream in;
        Retrofit retrofit;
        OkHttpClient httpClient;
        AssetManager assetMgr;
        SSLContext sslCtx;
        X509KeyManager km;
        X509TrustManager tm;

          assetMgr = myCtx.getAssets();
// TODO - http://github.com/square/okhttp/issues/3519 - August 11, 2017
//        in = assetMgr.open("client.pem");
//        try {
//            km = SslExt.loadKeyManager(in);
//        } finally {
//            in = IOExt.close(in);
//        }
        km = null;

        in = assetMgr.open("ca.pem");
        try {
            tm = SslExt.loadTrustManager(in);
        } finally {
            in = IOExt.close(in);
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
