package it.scoppelletti.spaceship.cognito.sample;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import it.scoppelletti.spaceship.ExceptionEvent;
import it.scoppelletti.spaceship.app.ExceptionDialogFragment;
import it.scoppelletti.spaceship.app.NavigationDrawer;
import it.scoppelletti.spaceship.app.TitleAdapter;
import it.scoppelletti.spaceship.cognito.CognitoAdapter;
import it.scoppelletti.spaceship.widget.ProgressOverlay;

public final class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
    private NavigationDrawer myDrawer;
    private TitleAdapter myTitleAdapter;
    private ProgressOverlay myProgressBar;

    public MainActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        myDrawer = new NavigationDrawer.Builder(this)
                .layoutId(R.id.drawer_layout)
                .viewId(R.id.nav_view)
                .toolbarId(R.id.toolbar)
                .onItemSelectedIListener(this).build();

        setSupportActionBar(myDrawer.getToolbar());
        myDrawer.onCreate(savedInstanceState);
        myTitleAdapter = new TitleAdapter.Builder(this)
                .toolbarLayoutId(R.id.toolbar_layout).build();
        myProgressBar = (ProgressOverlay) findViewById(R.id.progress_bar);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        myDrawer.onPostCreate(savedInstanceState);
        myTitleAdapter.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
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
        case R.id.cmd_logout:
            CognitoAdapter.getInstance().logout();
            intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
            break;
        }

        return false;
    }

    @Subscribe
    public void onExceptionEvent(final @NonNull ExceptionEvent event) {
        myProgressBar.hide(new Runnable() {

            @Override
            public void run() {
                new ExceptionDialogFragment.Builder(MainActivity.this)
                        .exceptionEvent(event).show();
            }
        });
    }
}
