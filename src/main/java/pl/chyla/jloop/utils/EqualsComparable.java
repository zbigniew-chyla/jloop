/*
 * Copyright (C) 2018,2020, Zbigniew Chyla
 *
 * This file is part of jloop library. See LICENSE file for licensing information.
 */
package pl.chyla.jloop.utils;


public abstract class EqualsComparable<T extends EqualsComparable<T>> {

    @Override
    final public boolean equals(Object otherObj) {
        if (this == otherObj) {
            return true;
        }
        Class<T> objClass = getEqualsClass();
        return objClass.isInstance(otherObj) && equalsImpl(objClass.cast(otherObj));
    }


    @Override
    final public int hashCode() {
        return hashCodeImpl();
    }


    protected abstract Class<T> getEqualsClass();

    protected abstract boolean equalsImpl(T other);

    protected abstract int hashCodeImpl();
}
