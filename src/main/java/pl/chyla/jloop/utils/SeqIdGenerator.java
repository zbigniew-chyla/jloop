/*
 * Copyright (C) 2018,2020, Zbigniew Chyla
 *
 * This file is part of jloop library. See LICENSE file for licensing information.
 */
package pl.chyla.jloop.utils;


public final class SeqIdGenerator {

    private long nextSeqId;


    public SeqIdGenerator() {
        nextSeqId = Long.MIN_VALUE;
    }


    public long generateNext() {
        long seqId = nextSeqId;
        nextSeqId++;
        return seqId;
    }
}
