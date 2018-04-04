
package it.scoppelletti.spaceship.cognito.sample;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import it.scoppelletti.spaceship.rx.SingleCoordinator;

public final class MainActivityData extends Fragment {
    public static final String TAG = MainApp.TAG_MAINACTIVITYDATA;

    private SingleCoordinator<CognitoUserSession> mySessionCoordinator;

    public MainActivityData() {
        setRetainInstance(true);
    }

    @NonNull
    SingleCoordinator<CognitoUserSession> getSessionCoordinator() {
        if (mySessionCoordinator == null) {
            mySessionCoordinator = new SingleCoordinator<>();
        }

        return mySessionCoordinator;
    }

    @Override
    public void onDestroy() {
        if (mySessionCoordinator != null) {
            mySessionCoordinator.onDestroy();
            mySessionCoordinator = null;
        }

        super.onDestroy();
    }
}
