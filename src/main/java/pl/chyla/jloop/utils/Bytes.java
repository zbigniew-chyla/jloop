/*
 * Copyright (C) 2018,2020, Zbigniew Chyla
 *
 * This file is part of jloop library. See LICENSE file for licensing information.
 */
package pl.chyla.jloop.utils;


public final class Bytes {

    private Bytes() {
    }


    public static String toHexString(byte[] bytes) {
        StringBuilder hexBuilder = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            hexBuilder.append(nibbleToHex((b >> 4) & 0x0F));
            hexBuilder.append(nibbleToHex(b & 0x0F));
        }
        return hexBuilder.toString();
    }


    private static char nibbleToHex(int value) {
        return value <= 9
            ? (char) ('0' + value)
            : (char) ('A' + value - 10);
    }
}
