/*
 * Copyright (C) 2018,2020, Zbigniew Chyla
 *
 * This file is part of jloop library. See LICENSE file for licensing information.
 */
package pl.chyla.jloop.core;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.util.ArrayList;
import pl.chyla.jloop.utils.Sockets;


public final class EventLoopMTGateway implements Closeable {

    private final EventLoop evloop;
    private final ArrayList<Runnable> tasksQueue;
    private final Pipe notifyPipe;
    private final ActivityHandle notifyfPipeHandle;
    private final ByteBuffer notifyMsgBuffer;


    public EventLoopMTGateway(IOEventLoop evloop) throws IOException {
        this.evloop = evloop;
        tasksQueue = new ArrayList<Runnable>();
        notifyPipe = Pipe.open();
        notifyPipe.sink().configureBlocking(false);
        notifyPipe.source().configureBlocking(false);
        notifyfPipeHandle = evloop.addIOWatch(
            notifyPipe.source(), IOWatchMode.READ, this::pullEnqueuedTasksToEventLoop);
        notifyMsgBuffer = ByteBuffer.allocate(1);
    }


    public void enqueueTask(Runnable task) {
        synchronized (tasksQueue) {
            tasksQueue.add(task);
            if (tasksQueue.size() == 1) {
                notifyMsgBuffer.clear();
                notifyMsgBuffer.put((byte) 0);
                notifyMsgBuffer.flip();
                try {
                    notifyPipe.sink().write(notifyMsgBuffer);
                } catch (IOException e) {
                    throw new RuntimeException(
                        "Error writing no notification pipe. It should never happen.");
                }
            }
        }
    }


    private void pullEnqueuedTasksToEventLoop() {
        synchronized (tasksQueue) {
            notifyMsgBuffer.clear();
            try {
                notifyPipe.source().read(notifyMsgBuffer);
            } catch (IOException e) {
                throw new RuntimeException(
                    "Error reading from notification pipe. It should never happen.");
            }
            for (Runnable task : tasksQueue) {
                evloop.invokeLater(task);
            }
            tasksQueue.clear();
        }
    }


    @Override
    public void close() throws IOException {
        notifyfPipeHandle.deactivate();
        Sockets.forceClose(notifyPipe.sink());
        Sockets.forceClose(notifyPipe.source());
    }
}
