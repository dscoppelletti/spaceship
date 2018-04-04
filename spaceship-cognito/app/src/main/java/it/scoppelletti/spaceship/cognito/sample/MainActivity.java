package it.scoppelletti.spaceship.cognito.sample;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import it.scoppelletti.spaceship.ApplicationException;
import it.scoppelletti.spaceship.ExceptionEvent;
import it.scoppelletti.spaceship.app.AppExt;
import it.scoppelletti.spaceship.app.ExceptionDialogFragment;
import it.scoppelletti.spaceship.app.NavigationDrawer;
import it.scoppelletti.spaceship.app.TitleAdapter;
import it.scoppelletti.spaceship.cognito.CognitoAdapter;
import it.scoppelletti.spaceship.cognito.app.ChangePasswordActivity;
import it.scoppelletti.spaceship.cognito.app.VerifyAttributeActivity;
import it.scoppelletti.spaceship.cognito.data.SpaceshipUser;
import it.scoppelletti.spaceship.cognito.data.UserAttribute;
import it.scoppelletti.spaceship.rx.CompleteEvent;
import it.scoppelletti.spaceship.rx.SingleCoordinator;
import it.scoppelletti.spaceship.widget.ProgressOverlay;

public final class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
    private NavigationDrawer myDrawer;
    private TitleAdapter myTitleAdapter;
    private TextView myUserLabel;
    private ProgressOverlay myProgressBar;
    private CompositeDisposable myDisposables;

    public MainActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        View view;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        myDrawer = new NavigationDrawer.Builder(this)
                .layoutId(R.id.drawer_layout)
                .viewId(R.id.nav_view)
                .toolbarId(R.id.toolbar)
                .onItemSelectedIListener(this).build();

        setSupportActionBar(myDrawer.getToolbar());
        myDrawer.onCreate(savedInstanceState);
        view = myDrawer.getNavigationView().getHeaderView(0);
        myUserLabel = view.findViewById(R.id.lbl_user);

        myTitleAdapter = new TitleAdapter.Builder(this)
                .toolbarLayoutId(R.id.toolbar_layout).build();
        myProgressBar = findViewById(R.id.progress_bar);
        myDisposables = new CompositeDisposable();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        myDrawer.onPostCreate(savedInstanceState);
        myTitleAdapter.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        Menu menu;
        MenuItem menuItem;
        Disposable subscription;
        SpaceshipUser user;
        SingleCoordinator<CognitoUserSession> coordinator;

        super.onResume();

        user = CognitoAdapter.getInstance().getCurrentUser();
        if (user != null) {
            myUserLabel.setText(user.getFullName());
        }

        menu = myDrawer.getNavigationView().getMenu();
        menuItem = menu.findItem(R.id.cmd_verifyEmail);
        menuItem.setEnabled(user != null &&
                !TextUtils.isEmpty(user.getEmail()) && !user.isEmailVerified());
        menuItem = menu.findItem(R.id.cmd_verifyPhoneNumber);
        menuItem.setEnabled(user != null &&
                !TextUtils.isEmpty(user.getPhoneNumber()) &&
                !user.isPhoneNumberVerified());

        EventBus.getDefault().register(this);

        coordinator = AppExt.getOrCreateFragment(this,
                MainActivityData.class, MainActivityData.TAG)
                .getSessionCoordinator();
        subscription = coordinator.subscribe(() -> new GetSessionObserver());
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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        myTitleAdapter.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (myDrawer.onBackPressed()) {
            return;
        }

        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        myDrawer.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (myDrawer.onOptionItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;

        myDrawer.closeDrawer();

        switch (item.getItemId()) {
        case R.id.cmd_changePassword:
            intent = new Intent(this, ChangePasswordActivity.class);
            startActivity(intent);
            break;

        case R.id.cmd_verifyEmail:
            intent = new Intent(this, VerifyAttributeActivity.class);
            intent.putExtra(CognitoAdapter.PROP_ATTRIBUTE,
                    UserAttribute.ATTR_EMAIL);
            startActivity(intent);
            break;

        case R.id.cmd_verifyPhoneNumber:
            intent = new Intent(this, VerifyAttributeActivity.class);
            intent.putExtra(CognitoAdapter.PROP_ATTRIBUTE,
                    UserAttribute.ATTR_PHONENUMBER);
            startActivity(intent);
            break;

        case R.id.cmd_getSession:
            onGetSessionClick();
            break;

        case R.id.cmd_logout:
            intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.putExtra(CognitoAdapter.PROP_LOGOUT, true);
            startActivity(intent);
            finish();
            break;
        }

        return false;
    }

    @Subscribe
    public void onCompleteEvent(@NonNull CompleteEvent event) {
        myProgressBar.hide();
    }

    @Subscribe
    public void onExceptionEvent(final @NonNull ExceptionEvent event) {
        myProgressBar.hide(() ->
                new ExceptionDialogFragment.Builder(MainActivity.this)
                        .exceptionEvent(event).show());
    }

    private void onGetSessionClick() {
        Disposable connection;
        SingleCoordinator<CognitoUserSession> coordinator;

        myProgressBar.show();
        try {
            coordinator = AppExt.getOrCreateFragment(this,
                    MainActivityData.class, MainActivityData.TAG)
                    .getSessionCoordinator();
            if (coordinator.isRunning()) {
                throw new ApplicationException.Builder(
                        R.string.err_getSessionAlreadyInProgress)
                        .title(R.string.cmd_getSession).build();
            }

            connection = coordinator.connect(Single.fromCallable(
                    () -> CognitoAdapter.getInstance().getSession())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()));
            myDisposables.add(connection);
        } catch (RuntimeException ex) {
            EventBus.getDefault().post(new ExceptionEvent(ex)
                    .title(R.string.cmd_getSession));
        }
    }
}
