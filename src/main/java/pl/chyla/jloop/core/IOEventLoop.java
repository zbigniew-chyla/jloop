/*
 * Copyright (C) 2018,2020, Zbigniew Chyla
 *
 * This file is part of jloop library. See LICENSE file for licensing information.
 */
package pl.chyla.jloop.core;

import java.nio.channels.SelectableChannel;


public interface IOEventLoop extends EventLoop {

    ActivityHandle addIOWatch(SelectableChannel channel, IOWatchMode mode, Runnable notifyAction);


    default ActivityHandle addOneShotIOWatch(SelectableChannel channel, IOWatchMode mode, Runnable notifyAction)
    {
        ActivityHandle[] handleContainer = new ActivityHandle[1];
        handleContainer[0] = addIOWatch(
            channel, mode, () -> {
                handleContainer[0].deactivate();
                notifyAction.run();
            });
        return handleContainer[0];
    }
}
