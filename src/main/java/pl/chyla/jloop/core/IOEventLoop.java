/*
 * Copyright (C) 2018,2020, Zbigniew Chyla
 *
 * This file is part of jloop library. See LICENSE file for licensing information.
 */
package pl.chyla.jloop.core;

import java.nio.channels.SelectableChannel;
import pl.chyla.jloop.utils.ValueBox;


public interface IOEventLoop extends EventLoop {

    ActivityHandle addIOWatch(SelectableChannel channel, IOWatchMode mode, Runnable notifyAction);


    default ActivityHandle addOneShotIOWatch(SelectableChannel channel, IOWatchMode mode, Runnable notifyAction)
    {
        ValueBox<ActivityHandle> handleBox = new ValueBox<ActivityHandle>();
        handleBox.setValue(
            addIOWatch(
                channel, mode, () -> {
                    handleBox.getValue().deactivate();
                    notifyAction.run();
                }));
        return handleBox.getValue();
    }
}
