/*
 * Copyright (C) 2018,2020, Zbigniew Chyla
 *
 * This file is part of jloop library. See LICENSE file for licensing information.
 */
package pl.chyla.jloop.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


public final class Lists {

    private Lists() {
    }


    public static <In, Out> ArrayList<Out> transform(List<In> input, Function<In, Out> transformFunc) {
        ArrayList<Out> output = new ArrayList<Out>(input.size());
        for (In elem : input) {
            output.add(transformFunc.apply(elem));
        }
        return output;
    }
}
