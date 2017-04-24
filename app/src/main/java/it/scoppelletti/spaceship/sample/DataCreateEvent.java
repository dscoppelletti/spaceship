package it.scoppelletti.spaceship.sample;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class DataCreateEvent {

    @Getter
    private final long myId;
}
