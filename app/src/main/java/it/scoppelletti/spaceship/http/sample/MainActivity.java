package it.scoppelletti.spaceship.http.sample;

import android.content.Context;
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
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import it.scoppelletti.spaceship.app.AppExt;
import it.scoppelletti.spaceship.app.ExceptionDialogFragment;
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

        toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        subscription = coordinator.subscribe(GreetingObserver.newFactory());

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
        Disposable connection;
        SingleCoordinator<String> coordinator;

        try {
            coordinator = AppExt.getOrCreateFragment(this,
                    MainActivityData.class, MainActivityData.TAG)
                    .getGreetingCoordinator();
            connection = coordinator.connect(Single.fromCallable(
                    new GreetingCallable(this))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()));
            myDisposables.add(connection);
        } catch (RuntimeException ex) {
            new ExceptionDialogFragment.Builder(this)
                    .throwable(ex)
                    .show();
        }
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
