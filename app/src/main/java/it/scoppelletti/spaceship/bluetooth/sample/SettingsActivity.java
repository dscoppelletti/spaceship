package it.scoppelletti.spaceship.bluetooth.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import it.scoppelletti.spaceship.app.UpNavigationProvider;

public class SettingsActivity extends AppCompatActivity {
    private UpNavigationProvider myNavProvider;

    public SettingsActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Toolbar toolbar;

        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myNavProvider = new UpNavigationProvider.Builder(this).build();
        myNavProvider.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new SettingsFragment())
                .commit();
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
}
