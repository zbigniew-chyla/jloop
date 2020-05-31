/*
 * Copyright (C) 2018,2020, Zbigniew Chyla
 *
 * This file is part of jloop library. See LICENSE file for licensing information.
 */
package pl.chyla.jloop.core;


final class MonotonicClock {

    private MonotonicClock() {
    }


    public static long getTime() {
        return System.nanoTime() / 1000000;
    }
}
