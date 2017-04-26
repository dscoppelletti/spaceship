package it.scoppelletti.spaceship.bluetooth.sample;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import it.scoppelletti.spaceship.ApplicationException;
import it.scoppelletti.spaceship.ExceptionEvent;
import it.scoppelletti.spaceship.app.AppExt;
import it.scoppelletti.spaceship.app.ExceptionDialogFragment;
import it.scoppelletti.spaceship.bluetooth.BluetoothExt;
import it.scoppelletti.spaceship.bluetooth.sample.data.PrintForm;
import it.scoppelletti.spaceship.bluetooth.sample.data.Printer;
import it.scoppelletti.spaceship.bluetooth.sample.databinding.MainActivityBinding;
import it.scoppelletti.spaceship.rx.CompletableCoordinator;
import it.scoppelletti.spaceship.rx.CompletableObserverFactory;
import it.scoppelletti.spaceship.rx.ExponentialRetry;
import it.scoppelletti.spaceship.types.StringExt;
import it.scoppelletti.spaceship.widget.ProgressOverlay;
import it.scoppelletti.spaceship.widget.SnackbarEvent;

public class MainActivity extends AppCompatActivity implements
        TextView.OnEditorActionListener {
    private static final String PROP_FORM = "1";
    private static final int REQ_PRINT = 1;
    private ProgressOverlay myProgressBar;
    private MainActivityBinding myBinding;
    private CompositeDisposable myDisposables;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toolbar toolbar;
        PrintForm form;
        FloatingActionButton fab;

        super.onCreate(savedInstanceState);
        myBinding = DataBindingUtil.setContentView(this,
                R.layout.main_activity);
        myDisposables = new CompositeDisposable();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myProgressBar = (ProgressOverlay) findViewById(R.id.progress_bar);

        if (savedInstanceState == null) {
            form = new PrintForm();
        } else {
            form = savedInstanceState.getParcelable(MainActivity.PROP_FORM);
        }

        myBinding.setForm(form);
        myBinding.txtBody.setOnEditorActionListener(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onPrintClick();
            }
        });
    }

    @Override
    protected void onResume() {
        Disposable subscription;
        CompletableCoordinator coordinator;

        super.onResume();
        EventBus.getDefault().register(this);

        coordinator = AppExt.getOrCreateFragment(this,
                MainActivityData.class, MainActivityData.TAG)
                .getPrinter();
        subscription = coordinator.subscribe(new CompletableObserverFactory() {

            @NonNull
            @Override
            public DisposableCompletableObserver create() {
                return new MainActivity.PrintObserver();
            }
        });

        myDisposables.add(subscription);
    }

    @Override
    protected void onPause() {
        myDisposables.dispose();
        myDisposables = new CompositeDisposable();

        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        PrintForm form;

        super.onSaveInstanceState(outState);

        if (myBinding != null) {
            form = myBinding.getForm();
            if (form != null) {
                outState.putParcelable(MainActivity.PROP_FORM, form);
            }
        }
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
        Intent intent;

        switch (item.getItemId()) {
        case R.id.cmd_setting:
            intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
        case MainActivity.REQ_PRINT:
            onPrintClick();
            break;
        }
    }

    @Override
    public boolean onEditorAction(@NonNull TextView v, int actionId,
            @Nullable KeyEvent event) {
      switch (actionId) {
        case EditorInfo.IME_ACTION_DONE:
            onPrintClick();
            return true;
        }

        return false;
    }

    private void onPrintClick() {
        String deviceAddress;
        PrintForm form;
        Disposable connection;
        Completable printer;
        SharedPreferences prefs;
        CompletableCoordinator coordinator;

        AppExt.hideSoftKeyboard(this);

        form = myBinding.getForm();
        if (!form.getBodyValidator().validate()) {
            return;
        }

        try {
            prefs = PreferenceManager.getDefaultSharedPreferences(this);
            deviceAddress = prefs.getString(getString(R.string.pref_device),
                    StringExt.EMPTY);
            if (TextUtils.isEmpty(deviceAddress)) {
                throw new ApplicationException.Builder(
                        R.string.it_scoppelletti_err_btNoDeviceSelected)
                        .build();
            }

            if (!BluetoothExt.enable(this, MainActivity.REQ_PRINT)) {
                return;
            }

            coordinator = AppExt.getOrCreateFragment(this,
                    MainActivityData.class, MainActivityData.TAG)
                    .getPrinter();
            if (coordinator.isRunning()) {
                throw new ApplicationException.Builder(R.string.err_printerBusy)
                        .build();
            }

            printer = Printer.newInstance(this, deviceAddress, form.getBody());
            connection = coordinator.connect(
                    printer.retryWhen(new ExponentialRetry(3))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()));
            myDisposables.add(connection);
        } catch (RuntimeException ex) {
            EventBus.getDefault().post(new ExceptionEvent(ex));
        }
    }

    @Subscribe
    public void onPrintEvent(@NonNull PrintEvent event) {
        myProgressBar.show();
    }

    @Subscribe
    public void onSnackbarEvent(final @NonNull SnackbarEvent event) {
        myProgressBar.hide(new Runnable() {

            @Override
            public void run() {
                event.show(myBinding.txtBody);
            }
        });
    }

    @Subscribe
    public void onExceptionEvent(final @NonNull ExceptionEvent event) {
        myProgressBar.hide(new Runnable() {

            @Override
            public void run() {
                new ExceptionDialogFragment.Builder(MainActivity.this)
                        .exceptionEvent(event)
                        .show();
            }
        });
    }

    private static final class PrintObserver extends
            DisposableCompletableObserver {

        @Override
        protected void onStart() {
            EventBus.getDefault().post(PrintEvent.getInstance());
        }

        @Override
        public void onComplete() {
            EventBus.getDefault().post(new SnackbarEvent(
                    R.string.msg_printSent, Snackbar.LENGTH_SHORT));
        }

        @Override
        public void onError(Throwable ex) {
            EventBus.getDefault().post(new ExceptionEvent(ex)
                .title(R.string.cmd_print));
        }
    }
}
