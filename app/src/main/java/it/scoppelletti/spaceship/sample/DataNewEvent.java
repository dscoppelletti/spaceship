package it.scoppelletti.spaceship.sample;

public final class DataNewEvent {
    private static final DataNewEvent myInstance = new DataNewEvent();

    private DataNewEvent() {
    }

    public static DataNewEvent getInstance() {
        return myInstance;
    }
}
