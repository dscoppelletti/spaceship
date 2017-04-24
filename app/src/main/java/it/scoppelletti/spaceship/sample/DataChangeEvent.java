package it.scoppelletti.spaceship.sample;

public final class DataChangeEvent {
    private static final DataChangeEvent myInstance = new DataChangeEvent();

    private DataChangeEvent() {
    }

    public static DataChangeEvent getInstance() {
        return myInstance;
    }
}
