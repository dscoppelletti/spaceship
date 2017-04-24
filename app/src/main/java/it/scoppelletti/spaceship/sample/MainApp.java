package it.scoppelletti.spaceship.sample;

import android.app.Application;
import org.greenrobot.eventbus.EventBus;

public final class MainApp extends Application {
    public static final String PROP_DATAID =
            "it.scoppelletti.spaceship.sample.1";
    public static final String PROP_LISTPOS =
            "it.scoppelletti.spaceship.sample.2";
    public static final String PROP_SECTION =
            "it.scoppelletti.spaceship.sample.3";
    public static final String PROP_TABPOS =
            "it.scoppelletti.spaceship.sample.4";
    public static final String TAG_DRAWERACTIVITYDATA =
            "it.scoppelletti.spaceship.sample.1";
    public static final String TAG_LISTSECTION =
            "it.scoppelletti.spaceship.sample.2";
    public static final String TAG_FORMTAB =
            "it.scoppelletti.spaceship.sample.3";
    public static final String TAG_TABBEDACTIVITYDATA =
            "it.scoppelletti.spaceship.sample.4";

    @Override
    public void onCreate() {
        super.onCreate();

        EventBus.builder()
                .sendSubscriberExceptionEvent(false)
                .sendNoSubscriberEvent(false)
                .eventInheritance(false)
                .throwSubscriberException(BuildConfig.DEBUG)
                .addIndex(new MainEventBusIndex())
                .installDefaultEventBus();
    }
}
