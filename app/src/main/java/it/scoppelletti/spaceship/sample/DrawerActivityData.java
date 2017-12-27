package it.scoppelletti.spaceship.sample;

import java.util.List;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import it.scoppelletti.spaceship.app.AppExt;
import it.scoppelletti.spaceship.rx.SingleCoordinator;
import it.scoppelletti.spaceship.sample.data.DataViewModel;

public final class DrawerActivityData extends Fragment {
    public static final String TAG = MainApp.TAG_DRAWERACTIVITYDATA;
    private SingleCoordinator<List<DataViewModel>> myLister;

    public DrawerActivityData() {
        setRetainInstance(true);
    }

    @NonNull
    public static DrawerActivityData getInstance(
            @NonNull FragmentActivity activity) {
        if (activity == null) {
            throw new NullPointerException("Argument activity is null.");
        }

        return AppExt.getOrCreateFragment(activity, DrawerActivityData.class,
                DrawerActivityData.TAG);
    }

    @NonNull
    public SingleCoordinator<List<DataViewModel>> getLister() {
        if (myLister == null) {
            myLister = new SingleCoordinator<>();
        }

        return myLister;
    }

    @Override
    public void onDestroy() {
        if (myLister != null) {
            myLister.onDestroy();
            myLister = null;
        }

        super.onDestroy();
    }
}
