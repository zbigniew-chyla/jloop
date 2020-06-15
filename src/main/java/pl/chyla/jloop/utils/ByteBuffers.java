/*
 * Copyright (C) 2018,2020, Zbigniew Chyla
 *
 * This file is part of jloop library. See LICENSE file for licensing information.
 */
package pl.chyla.jloop.utils;

import java.nio.ByteBuffer;


public final class ByteBuffers {

    private static final ByteBuffer emptyByteBuffer = ByteBuffer.wrap(new byte[0]);


    private ByteBuffers() {
    }


    public static ByteBuffer empty() {
        return emptyByteBuffer;
    }
}
