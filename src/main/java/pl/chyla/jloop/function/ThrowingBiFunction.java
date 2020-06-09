/*
 * Copyright (C) 2018,2020, Zbigniew Chyla
 *
 * This file is part of jloop library. See LICENSE file for licensing information.
 */
package pl.chyla.jloop.function;


@FunctionalInterface
public interface ThrowingBiFunction<E extends Exception, T, U, R> {

    R apply(T t, U u) throws E;
}
