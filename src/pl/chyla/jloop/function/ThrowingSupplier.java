/*
 * Copyright (C) 2018,2020, Zbigniew Chyla
 *
 * This file is part of jloop library. See LICENSE file for licensing information.
 */
package pl.chyla.jloop.function;


@FunctionalInterface
public interface ThrowingSupplier<E extends Exception, T> {

    T get() throws E;
}
