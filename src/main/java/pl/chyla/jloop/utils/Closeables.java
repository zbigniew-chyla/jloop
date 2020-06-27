/*
 * Copyright (C) 2018,2020, Zbigniew Chyla
 *
 * This file is part of jloop library. See LICENSE file for licensing information.
 */
package pl.chyla.jloop.utils;

import java.io.Closeable;
import java.io.IOException;


public final class Closeables {

    private Closeables() {
    }


    public static void forceClose(Closeable obj) {
        try {
            obj.close();
        } catch (IOException e) {
            // ignore
        }
    }
}
