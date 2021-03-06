/*
 * Copyright (C) 2018,2020, Zbigniew Chyla
 *
 * This file is part of jloop library. See LICENSE file for licensing information.
 */
package pl.chyla.jloop.core;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import pl.chyla.jloop.utils.ValueBox;


public final class BasicIOEventLoop implements IOEventLoop {

    private final Selector selector;
    private final Map<SelectableChannel, ChannelWatchesManager> watchesManagers;
    private final Set<ChannelWatchesManager> clearedWatchesManagers;
    private final BasicEventLoop subLoop;
    private final EventLoopMTGateway mtGateway;
    private final Consumer<Runnable> mtExecutor;
    private long time;


    public BasicIOEventLoop() throws IOException {
        selector = Selector.open();
        watchesManagers = new HashMap<SelectableChannel, ChannelWatchesManager>();
        clearedWatchesManagers = new HashSet<ChannelWatchesManager>();
        subLoop = new BasicEventLoop(MonotonicClock.getTime());
        time = subLoop.getTime();
        mtGateway = new EventLoopMTGateway(this);
        mtExecutor = (task) -> {
            mtGateway.enqueueTask(task);
        };
    }


    public void runIterations() throws IOException {
        while (true) {
            subLoop.processTimeUpdate(time);

            for (ChannelWatchesManager watchesManager : clearedWatchesManagers) {
                SelectionKey selectionKey = watchesManager.getSelectionKey();
                watchesManagers.remove(selectionKey.channel());
                selectionKey.cancel();
            }
            clearedWatchesManagers.clear();

            long now = MonotonicClock.getTime();
            long nextWakeupTimeOrMax = subLoop.getNextWakeupTimeOrMax();
            if (nextWakeupTimeOrMax <= now) {
                selector.selectNow();
            } else if (nextWakeupTimeOrMax < Long.MAX_VALUE) {
                selector.select(nextWakeupTimeOrMax - now);
            } else {
                selector.select();
            }

            time = MonotonicClock.getTime();

            for (SelectionKey selectedKey : selector.selectedKeys()) {
                ChannelWatchesManager watchesManager = watchesManagers.get(selectedKey.channel());
                watchesManager.handleSelection();
            }
            selector.selectedKeys().clear();
        }
    }


    /**
     * Returns tasks executor that can be used from any thread.
     *
     * Returns {@link Consumer} object that can be used from any thread to start a task in the
     * event loop (on its thread). Calling {@link Consumer#accept(Object))} queues a task for
     * execution.
     *
     * @return multi-thread safe tasks executor
     */
    public Consumer<Runnable> getMtExecutor() {
        return mtExecutor;
    }


    @Override
    public ActivityHandle addIOWatch(SelectableChannel channel, IOWatchMode mode, Runnable notifyAction) {
        if (!channel.isOpen()) {
            throw new IllegalArgumentException("Adding IO watch for closed channel");
        }
        if (channel.isBlocking()) {
            throw new IllegalArgumentException("Adding IO watch for channel in blocking mode");
        }
        ChannelWatchesManager watchesManager = watchesManagers.get(channel);
        if (watchesManager == null) {
            ValueBox<ChannelWatchesManager> watchesManagerBox = new ValueBox<ChannelWatchesManager>();
            watchesManagerBox.setValue(
                new ChannelWatchesManager(
                    makeEmptySelectionKey(channel),
                    () -> clearedWatchesManagers.add(watchesManagerBox.getValue())));
            watchesManager = watchesManagerBox.getValue();
            watchesManagers.put(channel, watchesManager);
        }

        clearedWatchesManagers.remove(watchesManager);

        ActivityHandle handle = watchesManager.addIOWatch(mode, notifyAction);
        return handle;
    }


    private SelectionKey makeEmptySelectionKey(SelectableChannel channel) {
        SelectionKey selectionKey;
        try {
            selectionKey = channel.register(selector, 0);
        } catch (ClosedChannelException e) {
            throw new RuntimeException(e);
        }
        return selectionKey;
    }


    @Override
    public ActivityHandle invokeAt(long time, Runnable task) {
        return subLoop.invokeAt(time, task);
    }


    @Override
    public long getTime() {
        return time;
    }
}
