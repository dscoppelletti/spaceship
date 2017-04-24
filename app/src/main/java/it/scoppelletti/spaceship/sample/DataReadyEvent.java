package it.scoppelletti.spaceship.sample;

public class DataReadyEvent {
    private static final DataReadyEvent myInstance = new DataReadyEvent();

    private DataReadyEvent() {
    }

    public static DataReadyEvent getInstance() {
        return myInstance;
    }
}
