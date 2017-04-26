package it.scoppelletti.spaceship.bluetooth.sample;

public class PrintEvent {
    private static final PrintEvent myInstance = new PrintEvent();

    private PrintEvent() {
    }

    public static PrintEvent getInstance() {
        return myInstance;
    }
}
