package it.scoppelletti.spaceship.bluetooth.sample;

import android.app.Application;
import org.greenrobot.eventbus.EventBus;

public final class MainApp extends Application {

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
