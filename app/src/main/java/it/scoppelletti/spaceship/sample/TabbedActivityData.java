package it.scoppelletti.spaceship.sample;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import it.scoppelletti.spaceship.app.AppExt;
import it.scoppelletti.spaceship.rx.CompletableCoordinator;
import it.scoppelletti.spaceship.rx.SingleCoordinator;
import it.scoppelletti.spaceship.sample.data.DataViewModel;

public final class TabbedActivityData extends Fragment {
    public static final String TAG = MainApp.TAG_TABBEDACTIVITYDATA;
    private SingleCoordinator<DataViewModel> myCreator;
    private SingleCoordinator<DataViewModel> myReader;
    private SingleCoordinator<DataViewModel> myUpdater;
    private CompletableCoordinator myDeleter;

    public TabbedActivityData() {
        setRetainInstance(true);
    }

    @NonNull
    public static TabbedActivityData getInstance(
            @NonNull FragmentActivity activity) {
        if (activity == null) {
            throw new NullPointerException("Argument activity is null.");
        }

        return AppExt.getOrCreateFragment(activity, TabbedActivityData.class,
                TabbedActivityData.TAG);
    }

    @NonNull
    public SingleCoordinator<DataViewModel> getCreator() {
        if (myCreator == null) {
            myCreator = new SingleCoordinator<>();
        }

        return myCreator;
    }

    @NonNull
    public SingleCoordinator<DataViewModel> getReader() {
        if (myReader == null) {
            myReader = new SingleCoordinator<>();
        }

        return myReader;
    }

    @NonNull
    public SingleCoordinator<DataViewModel> getUpdater() {
        if (myUpdater == null) {
            myUpdater = new SingleCoordinator<>();
        }

        return myUpdater;
    }

    @NonNull
    public CompletableCoordinator getDeleter() {
        if (myDeleter == null) {
            myDeleter = new CompletableCoordinator();
        }

        return myDeleter;
    }

    @Override
    public void onDestroy() {
        if (myCreator != null) {
            myCreator.onDestroy();
            myCreator = null;
        }
        if (myReader != null) {
            myReader.onDestroy();
            myReader = null;
        }
        if (myUpdater != null) {
            myUpdater.onDestroy();
            myUpdater = null;
        }
        if (myDeleter != null) {
            myDeleter.onDestroy();
            myDeleter = null;
        }

        super.onDestroy();
    }
}
