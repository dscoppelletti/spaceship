package it.scoppelletti.spaceship.sample;

public class DataAccessEvent {
    private static final DataAccessEvent myInstance = new DataAccessEvent();

    private DataAccessEvent() {
    }

    public static DataAccessEvent getInstance() {
        return myInstance;
    }
}
