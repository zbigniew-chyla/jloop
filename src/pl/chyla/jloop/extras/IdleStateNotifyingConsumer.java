/*
 * Copyright (C) 2018,2020, Zbigniew Chyla
 *
 * This file is part of jloop library. See LICENSE file for licensing information.
 */
package pl.chyla.jloop.extras;

import java.util.function.Consumer;
import pl.chyla.jloop.core.ActivityHandle;
import pl.chyla.jloop.core.EventLoop;


public final class IdleStateNotifyingConsumer<T> implements Consumer<T> {

    public enum State {
        ACTIVE,
        IDLE,
    }

    private final EventLoop evloop;
    private final Consumer<T> baseConsumer;
    private final long timeToIdle;
    private final Consumer<State> stateChangeConsumer;
    private ActivityHandle idleTimeoutHandle;


    private IdleStateNotifyingConsumer(EventLoop evloop, Consumer<T> baseConsumer, long timeToIdle, Consumer<State> stateChangeConsumer) {
        this.evloop = evloop;
        this.baseConsumer = baseConsumer;
        this.timeToIdle = timeToIdle;
        this.stateChangeConsumer = stateChangeConsumer;
        idleTimeoutHandle = null;
        restartIdleTimeoutTimer();
    }


    public static <T> Consumer<T> make(EventLoop evloop, Consumer<T> baseConsumer, long timeToIdle, Consumer<State> stateChangeConsumer) {
        return new IdleStateNotifyingConsumer<T>(evloop, baseConsumer, timeToIdle, stateChangeConsumer);
    }


    private void restartIdleTimeoutTimer() {
        if (idleTimeoutHandle != null) {
            idleTimeoutHandle.deactivate();
        }
        idleTimeoutHandle = evloop.invokeLater(
            timeToIdle,
            () -> {
                idleTimeoutHandle = null;
                stateChangeConsumer.accept(State.IDLE);
            });
    }


    @Override
    public void accept(T value) {
        boolean wasIdle = idleTimeoutHandle == null;
        restartIdleTimeoutTimer();
        if (wasIdle) {
            stateChangeConsumer.accept(State.ACTIVE);
        }
        baseConsumer.accept(value);
    }
}
