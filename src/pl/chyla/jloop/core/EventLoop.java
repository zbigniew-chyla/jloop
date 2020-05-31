/*
 * Copyright (C) 2018,2020, Zbigniew Chyla
 *
 * This file is part of jloop library. See LICENSE file for licensing information.
 */
package pl.chyla.jloop.core;


public interface EventLoop {

    ActivityHandle invokeAt(long time, Runnable task);

    long getTime();


    default ActivityHandle invokeLater(long delay, Runnable task) {
        return invokeAt(getTime() + delay, task);
    }


    default ActivityHandle invokeLater(Runnable task) {
        return invokeLater(0, task);
    }
}
