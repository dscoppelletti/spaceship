package it.scoppelletti.spaceship.sample;

public class DataDeleteEvent {
    private static final DataDeleteEvent myInstance = new DataDeleteEvent();

    private DataDeleteEvent() {
    }

    public static DataDeleteEvent getInstance() {
        return myInstance;
    }
}
