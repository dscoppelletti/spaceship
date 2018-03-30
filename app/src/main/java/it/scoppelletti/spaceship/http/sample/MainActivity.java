package it.scoppelletti.spaceship.http.sample;

import java.io.InputStream;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import it.scoppelletti.spaceship.app.AppExt;
import it.scoppelletti.spaceship.app.ExceptionDialogFragment;
import it.scoppelletti.spaceship.http.ClientInterceptor;
import it.scoppelletti.spaceship.http.SslExt;
import it.scoppelletti.spaceship.io.IOExt;
import it.scoppelletti.spaceship.rx.SingleCoordinator;

public class MainActivity extends AppCompatActivity {
    private View myContentFrame;
    private CompositeDisposable myDisposables;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toolbar toolbar;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        myDisposables = new CompositeDisposable();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myContentFrame = findViewById(R.id.content_frame);
    }

    @Override
    protected void onResume() {
        Disposable subscription;
        SingleCoordinator<String> coordinator;

        super.onResume();

        coordinator = AppExt.getOrCreateFragment(this,
                MainActivityData.class, MainActivityData.TAG)
                .getGreetingCoordinator();
        subscription = coordinator.subscribe(() -> new GreetingObserver());

        myDisposables.add(subscription);
    }

    @Override
    protected void onPause() {
        myDisposables.dispose();
        myDisposables = new CompositeDisposable();

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater;

        inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.cmd_http:
            onHttpClick();
            return true;

        case R.id.cmd_network_check:
            onNetworkCheckClick();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onHttpClick() {
        GreetingService client;
        Disposable connection;
        SingleCoordinator<String> coordinator;

        try {
            client = createClient();
            coordinator = AppExt.getOrCreateFragment(this,
                    MainActivityData.class, MainActivityData.TAG)
                    .getGreetingCoordinator();
            connection = coordinator.connect(client.getPublicGreeting()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()));
            myDisposables.add(connection);
        } catch (Exception ex) {
            new ExceptionDialogFragment.Builder(this)
                    .throwable(ex)
                    .show();
        }
    }

    private GreetingService createClient() throws Exception {
        Retrofit retrofit;
        OkHttpClient httpClient;
        OkHttpClient.Builder builder;

        builder = new OkHttpClient.Builder()
                .addInterceptor(new ClientInterceptor(getApplicationContext()));
        setSslContext(builder);
        httpClient = builder.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVICE_URL)
                .client(httpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        return retrofit.create(GreetingService.class);
    }

    private void setSslContext(OkHttpClient.Builder builder) throws Exception {
        InputStream certIn = null;
        InputStream keyIn = null;
        AssetManager assetMgr;
        SSLContext sslCtx;
        X509KeyManager km;
        X509TrustManager tm;

        // TODO - I would like run this in background... using architecture
        // components library?
        assetMgr = getAssets();
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
        builder.sslSocketFactory(sslCtx.getSocketFactory(), tm);
    }

    private void onNetworkCheckClick() {
        int msg;
        ConnectivityManager connMgr;
        NetworkInfo networkInfo;

        connMgr = (ConnectivityManager) getSystemService(
                Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            msg = R.string.msg_network_connected;
        } else {
            msg = R.string.msg_network_notConnected;
        }

        Snackbar.make(myContentFrame, msg, Snackbar.LENGTH_SHORT).show();
    }
}
