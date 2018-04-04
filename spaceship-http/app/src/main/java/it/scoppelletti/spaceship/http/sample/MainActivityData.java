package it.scoppelletti.spaceship.http.sample;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import it.scoppelletti.spaceship.rx.SingleCoordinator;

public final class MainActivityData extends Fragment {
    public static final String TAG = MainApp.TAG_MAINDATA;

    private SingleCoordinator<String> myGreetingCoordinator;

    public MainActivityData() {
        setRetainInstance(true);
    }

    @NonNull
    SingleCoordinator<String> getGreetingCoordinator() {
        if (myGreetingCoordinator == null) {
            myGreetingCoordinator = new SingleCoordinator<>();
        }

        return myGreetingCoordinator;
    }
}
