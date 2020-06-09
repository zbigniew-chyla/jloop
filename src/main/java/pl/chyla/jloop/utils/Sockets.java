/*
 * Copyright (C) 2018,2020, Zbigniew Chyla
 *
 * This file is part of jloop library. See LICENSE file for licensing information.
 */
package pl.chyla.jloop.utils;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;


public final class Sockets {

    private Sockets() {
    }


    public static void forceClose(Closeable obj) {
        try {
            obj.close();
        } catch (IOException e) {
            // ignore
        }
    }


    public static SocketChannel openNBSocket() throws IOException {
        SocketChannel socket = SocketChannel.open();
        try {
            socket.configureBlocking(false);
        } catch (IOException e) {
            forceClose(socket);
            throw e;
        }
        return socket;
    }


    public static SocketChannel openNBClientSocket(SocketAddress remoteAddr) throws IOException {
        SocketChannel socket = openNBSocket();
        try {
            socket.connect(remoteAddr);
        } catch (IOException e) {
            forceClose(socket);
            throw e;
        }
        return socket;
    }


    public static ServerSocketChannel openNBServerSocket() throws IOException {
        ServerSocketChannel socket = ServerSocketChannel.open();
        try {
            socket.configureBlocking(false);
            socket.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        } catch (IOException e) {
            forceClose(socket);
            throw e;
        }
        return socket;
    }


    public static ServerSocketChannel openNBServerSocket(SocketAddress bindAddr) throws IOException {
        ServerSocketChannel socket = openNBServerSocket();
        try {
            socket.bind(bindAddr);
        } catch (IOException e) {
            forceClose(socket);
            throw e;
        }
        return socket;
    }
}
