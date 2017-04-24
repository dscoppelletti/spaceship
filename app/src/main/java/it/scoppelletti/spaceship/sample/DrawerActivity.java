package it.scoppelletti.spaceship.sample;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import it.scoppelletti.spaceship.ExceptionEvent;
import it.scoppelletti.spaceship.app.DialogCloseEvent;
import it.scoppelletti.spaceship.app.ExceptionDialogFragment;
import it.scoppelletti.spaceship.app.NavigationDrawer;
import it.scoppelletti.spaceship.app.TitleAdapter;
import it.scoppelletti.spaceship.widget.ProgressOverlay;

public final class DrawerActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
    private NavigationDrawer myDrawer;
    private TitleAdapter myTitleAdapter;
    private ProgressOverlay myProgressBar;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        FloatingActionButton fab;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_activity);

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

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(DataNewEvent.getInstance());
            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        int itemId;
        Fragment fragment;
        FragmentManager fragmentMgr;

        super.onPostCreate(savedInstanceState);
        myDrawer.onPostCreate(savedInstanceState);
        myTitleAdapter.onPostCreate(savedInstanceState);

        fragmentMgr = getSupportFragmentManager();
        fragment = fragmentMgr.findFragmentById(R.id.content_frame);
        if (fragment == null) {
            itemId = getIntent().getIntExtra(MainApp.PROP_SECTION,
                    R.id.cmd_listSection);
            if (navigateToFragment(itemId)) {
                myDrawer.setCheckedItem(itemId);
            }
        }
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
        myDrawer.closeDrawer();
        return navigateToFragment(item.getItemId());
    }

    private boolean navigateToFragment(int itemId) {
        int titleId;
        String tag;
        Fragment fragment;
        FragmentManager fragmentMgr;

        switch (itemId) {
        case R.id.cmd_listSection:
            tag = ListSectionFragment.TAG;
            titleId = R.string.cmd_listSection;
            fragment = ListSectionFragment.newInstance();
            break;

        default:
            tag = null;
            titleId = View.NO_ID;
            fragment = null;
            break;
        }

        if (fragment != null) {
            fragmentMgr = getSupportFragmentManager();
            fragmentMgr.beginTransaction()
                    .replace(R.id.content_frame, fragment, tag)
                    .commit();
            myTitleAdapter.setTitle(titleId);
            return true;
        }

        return false;
    }

    @Subscribe
    public void onDataAccessEvent(@NonNull DataAccessEvent event) {
        myProgressBar.show();
    }

    @Subscribe
    public void onDataReadyEvent(@NonNull DataReadyEvent event) {
        myProgressBar.hide();
    }

    @Subscribe
    public void onExceptionEvent(final @NonNull ExceptionEvent event) {
        myProgressBar.hide(new Runnable() {

            @Override
            public void run() {
                new ExceptionDialogFragment.Builder(DrawerActivity.this)
                        .exceptionEvent(event).show();
            }
        });
    }

    @Subscribe
    public void onDialogCloseEvent(@NonNull DialogCloseEvent event) {
        if (event.getRequestCode() == R.id.cmd_exit) {
            if (!isFinishing()) {
                finish();
            }
        }
    }
}
