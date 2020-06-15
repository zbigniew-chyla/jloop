/*
 * Copyright (C) 2018,2020, Zbigniew Chyla
 *
 * This file is part of jloop library. See LICENSE file for licensing information.
 */
package pl.chyla.jloop.function;

import java.io.IOException;


@FunctionalInterface
public interface IOThrowingSupplier<T> extends ThrowingSupplier<IOException, T> {
}
