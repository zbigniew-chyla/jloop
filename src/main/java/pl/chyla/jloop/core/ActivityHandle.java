/*
 * Copyright (C) 2018,2020, Zbigniew Chyla
 *
 * This file is part of jloop library. See LICENSE file for licensing information.
 */
package pl.chyla.jloop.core;


public interface ActivityHandle {

    public static final ActivityHandle inactiveInstance = InactiveActivityHandle.instance;


    boolean isActive();

    void deactivate();


    default void deactivateIfActive() {
        if (isActive()) {
            deactivate();
        }
    }
}
