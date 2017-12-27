package it.scoppelletti.spaceship.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import it.scoppelletti.spaceship.ExceptionEvent;
import it.scoppelletti.spaceship.app.DialogCloseEvent;
import it.scoppelletti.spaceship.app.ExceptionDialogFragment;
import it.scoppelletti.spaceship.app.TitleAdapter;
import it.scoppelletti.spaceship.app.UpNavigationCallbacks;
import it.scoppelletti.spaceship.app.UpNavigationProvider;
import it.scoppelletti.spaceship.rx.CompleteEvent;
import it.scoppelletti.spaceship.rx.StartEvent;
import it.scoppelletti.spaceship.sample.widget.DataPagerAdapter;
import it.scoppelletti.spaceship.widget.ProgressOverlay;
import it.scoppelletti.spaceship.widget.SnackbarEvent;

@Slf4j
public final class TabbedActivity extends AppCompatActivity implements
        UpNavigationCallbacks {
    private long myDataId;
    private TitleAdapter myTitleAdapter;
    private UpNavigationProvider myNavProvider;
    private TabLayout myTabLayout;
    private ViewPager myViewPager;
    private ProgressOverlay myProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Intent intent;
        Toolbar toolbar;
        DataPagerAdapter pagerAdapter;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabbed_activity);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myTitleAdapter = new TitleAdapter.Builder(this).build();
        myNavProvider = new UpNavigationProvider.Builder(this)
                .callbacks(this).build();
        myNavProvider.onCreate(savedInstanceState);
        myProgressBar = findViewById(R.id.progress_bar);

        if (savedInstanceState == null) {
            intent = getIntent();
            myDataId = intent.getLongExtra(MainApp.PROP_DATAID, -1);
        } else {
            myDataId = savedInstanceState.getLong(MainApp.PROP_DATAID, -1);
        }

        pagerAdapter = new DataPagerAdapter(this, getSupportFragmentManager(),
                myDataId);
        myViewPager = findViewById(R.id.view_pager);
        myViewPager.setAdapter(pagerAdapter);

        myTabLayout = findViewById(R.id.tab_layout);
        myTabLayout.setupWithViewPager(myViewPager);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        myTitleAdapter.onPostCreate(savedInstanceState);

        myTitleAdapter.setTitle((myDataId < 0) ?
                R.string.it_scoppelletti_cmd_new :
                R.string.it_scoppelletti_cmd_edit);

        if (savedInstanceState != null) {
            myViewPager.setCurrentItem(savedInstanceState.getInt(
                    MainApp.PROP_TABPOS));
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

        outState.putLong(MainApp.PROP_DATAID, myDataId);
        outState.putInt(MainApp.PROP_TABPOS,
                myTabLayout.getSelectedTabPosition());
    }

    @Override
    public void onPrepareSupportNavigateUpTaskStack(
            @NonNull TaskStackBuilder builder) {
        myNavProvider.onPrepareSupportNavigateUpTaskStack(builder);
    }

    @Override
    public void supportNavigateUpTo(@NonNull Intent upIntent) {
        myNavProvider.supportNavigateUpTo(upIntent);
        super.supportNavigateUpTo(upIntent);
    }

    @Override
    public void prepareIntent(@NonNull Intent intent) {
        intent.putExtra(MainApp.PROP_DATAID, myDataId);
    }

    @Subscribe
    public void onDataChangeEvent(@NonNull DataChangeEvent event) {
        supportInvalidateOptionsMenu();
    }

    @Subscribe
    public void onStartEvent(@NonNull StartEvent event) {
        myProgressBar.show();
    }

    @Subscribe
    public void onDataCreateEvent(@NonNull final DataCreateEvent event) {
        myProgressBar.hide(() -> {
            myDataId = event.getId();
            myTitleAdapter.setTitle(R.string.it_scoppelletti_cmd_edit);
            supportInvalidateOptionsMenu();
            Snackbar.make(myViewPager, R.string.msg_dataCreated,
                    Snackbar.LENGTH_SHORT).show();
        });
    }

    @Subscribe
    public void onCompleteEvent(@NonNull CompleteEvent event) {
        myProgressBar.hide(() -> supportInvalidateOptionsMenu());
    }

    @Subscribe
    public void onDataDeleteEvent(@NonNull DataDeleteEvent event) {
        myProgressBar.hide(() -> {
            supportInvalidateOptionsMenu();
            Snackbar.make(myViewPager, R.string.msg_dataDeleted,
                    Snackbar.LENGTH_SHORT)
                    .addCallback(navigateUp()).show();
        });
    }

    @Subscribe
    public void onSnackbarEvent(final @NonNull SnackbarEvent event) {
        myProgressBar.hide(() -> {
            supportInvalidateOptionsMenu();
            event.show(myViewPager);
        });
    }

    @Subscribe
    public void onExceptionEvent(final @NonNull ExceptionEvent event) {
        myProgressBar.hide(() ->
                new ExceptionDialogFragment.Builder(TabbedActivity.this)
                        .exceptionEvent(event).show());
    }

    @Subscribe
    public void onDialogCloseEvent(@NonNull DialogCloseEvent event) {
        if (event.getRequestCode() == R.id.cmd_exit) {
            myNavProvider.navigateUp();
        }
    }

    private BaseTransientBottomBar.BaseCallback<Snackbar> navigateUp() {
        return new BaseTransientBottomBar.BaseCallback<Snackbar>() {

            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                myNavProvider.navigateUp();
            }
        };
    }
}
