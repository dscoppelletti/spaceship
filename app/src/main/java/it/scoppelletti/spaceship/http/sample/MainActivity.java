package it.scoppelletti.spaceship.http.sample;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private View myContentFrame;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toolbar toolbar;
        FloatingActionButton fab;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myContentFrame = findViewById(R.id.content_frame);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onNetworkCheckClick();
            }
        });
    }

    private void onNetworkCheckClick() {
        int msg;
        ConnectivityManager connMgr;
        NetworkInfo networkInfo;

        connMgr = (ConnectivityManager) getSystemService(
                Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            msg = R.string.network_connected;
        } else {
            msg = R.string.network_notConnected;
        }

        Snackbar.make(myContentFrame, msg, Snackbar.LENGTH_SHORT).show();
    }
}
