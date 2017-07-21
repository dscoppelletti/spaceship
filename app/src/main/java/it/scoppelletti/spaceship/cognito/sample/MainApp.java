package it.scoppelletti.spaceship.cognito.sample;

import android.app.Application;
import android.content.Context;
import com.amazonaws.regions.Regions;
import org.greenrobot.eventbus.EventBus;
import it.scoppelletti.spaceship.cognito.CognitoAdapter;
import it.scoppelletti.spaceship.security.SecureString;

public final class MainApp extends Application {
    
    @Override
    public void onCreate() {
        Context ctx;

        super.onCreate();

        EventBus.builder()
                .sendSubscriberExceptionEvent(false)
                .sendNoSubscriberEvent(false)
                .eventInheritance(false)
                .throwSubscriberException(BuildConfig.DEBUG)
                .addIndex(new MainEventBusIndex())
                .installDefaultEventBus();

        ctx = getApplicationContext();
        new CognitoAdapter.Builder(ctx)
                .poolId(BuildConfig.POOL_ID)
                .clientId(BuildConfig.CLIENT_ID)
                .clientSecret(new SecureString(BuildConfig.CLIENT_SECRET))
                .region(Regions.EU_WEST_1).build();
    }
}
