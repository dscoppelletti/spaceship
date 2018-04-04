package it.scoppelletti.spaceship.bluetooth.sample;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import it.scoppelletti.spaceship.rx.CompletableCoordinator;

public final class MainActivityData extends Fragment {
    public static final String TAG =
            "it.scoppelletti.spaceship.bluetooth.sample.mainActivityData";
    private CompletableCoordinator myPrinter;

    public MainActivityData() {
        setRetainInstance(true);
    }

    @NonNull
    public CompletableCoordinator getPrinter() {
        if (myPrinter == null) {
            myPrinter = new CompletableCoordinator();
        }

        return myPrinter;
    }

    @Override
    public void onDestroy() {
        if (myPrinter != null) {
            myPrinter.onDestroy();
            myPrinter = null;
        }

        super.onDestroy();
    }
}
