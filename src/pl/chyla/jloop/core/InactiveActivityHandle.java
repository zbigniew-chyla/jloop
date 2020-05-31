/*
 * Copyright (C) 2018,2020, Zbigniew Chyla
 *
 * This file is part of jloop library. See LICENSE file for licensing information.
 */
package pl.chyla.jloop.core;


final class InactiveActivityHandle implements ActivityHandle {

    public static final ActivityHandle instance = new InactiveActivityHandle();


    private InactiveActivityHandle() {
    }


    @Override
    public boolean isActive() {
        return false;
    }


    @Override
    public void deactivate() {
        throw new IllegalStateException("The handle is already deactivated");
    }
}
