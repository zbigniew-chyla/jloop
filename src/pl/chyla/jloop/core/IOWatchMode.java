/*
 * Copyright (C) 2018,2020, Zbigniew Chyla
 *
 * This file is part of jloop library. See LICENSE file for licensing information.
 */
package pl.chyla.jloop.core;

import java.nio.channels.SelectionKey;


public enum IOWatchMode {
    READ(SelectionKey.OP_READ),
    WRITE(SelectionKey.OP_WRITE),
    ACCEPT(SelectionKey.OP_ACCEPT),
    CONNECT(SelectionKey.OP_CONNECT);

    private final int selectionOp;


    private IOWatchMode(int selectionOp) {
        this.selectionOp = selectionOp;
    }


    public final int getSelectionOp() {
        return selectionOp;
    }
}
