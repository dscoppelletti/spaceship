package it.scoppelletti.spaceship.sample;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DataEditEvent {

    @Getter
    private final long myId;
}
