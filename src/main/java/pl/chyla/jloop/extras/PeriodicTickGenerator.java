/*
 * Copyright (C) 2018,2020, Zbigniew Chyla
 *
 * This file is part of jloop library. See LICENSE file for licensing information.
 */
package pl.chyla.jloop.extras;

import pl.chyla.jloop.core.ActivityHandle;
import pl.chyla.jloop.core.EventLoop;


public final class PeriodicTickGenerator {

    private final EventLoop evloop;
    private final long interval;
    private final Runnable tick;
    private long lastTickTime;
    private ActivityHandle tickHandle;


    private PeriodicTickGenerator(EventLoop evloop, long interval, Runnable tick) {
        this.evloop = evloop;
        this.interval = interval;
        this.tick = tick;
        lastTickTime = evloop.getTime();
        tickHandle = scheduleNextTick();
    }


    private ActivityHandle scheduleNextTick() {
        return evloop.invokeAt(
            lastTickTime + interval,
            () -> {
                lastTickTime += interval;
                tickHandle = scheduleNextTick();
                tick.run();
            });
    }


    public static ActivityHandle start(EventLoop evloop, long interval, Runnable tick) {
        PeriodicTickGenerator tickGenerator = new PeriodicTickGenerator(evloop, interval, tick);
        return tickGenerator.tickHandle;
    }
}
