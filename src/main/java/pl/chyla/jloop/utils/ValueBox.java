/*
 * Copyright (C) 2018,2020, Zbigniew Chyla
 *
 * This file is part of jloop library. See LICENSE file for licensing information.
 */
package pl.chyla.jloop.utils;


public final class ValueBox<E> {

    private E value;


    public ValueBox() {
        this.value = null;
    }


    public ValueBox(E value) {
        this.value = value;
    }


    public E getValue() {
        return value;
    }


    public void setValue(E value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return value.toString();
    }
}
