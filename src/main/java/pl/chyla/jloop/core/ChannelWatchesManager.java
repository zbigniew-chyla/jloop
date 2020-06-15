/*
 * Copyright (C) 2018,2020, Zbigniew Chyla
 *
 * This file is part of jloop library. See LICENSE file for licensing information.
 */
package pl.chyla.jloop.core;

import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.TreeSet;
import pl.chyla.jloop.utils.EqualsComparable;
import pl.chyla.jloop.utils.SeqIdGenerator;


final class ChannelWatchesManager {

    private final SelectionKey selectionKey;
    private final Runnable clearedListener;
    private final EnumMap<IOWatchMode, TreeSet<Watch>> watches;
    private final SeqIdGenerator watchSeqIdGenerator;


    public ChannelWatchesManager(SelectionKey selectionKey, Runnable clearedListener) {
        this.selectionKey = selectionKey;
        this.clearedListener = clearedListener;
        watches = new EnumMap<IOWatchMode, TreeSet<Watch>>(IOWatchMode.class);
        watchSeqIdGenerator = new SeqIdGenerator();
    }


    public SelectionKey getSelectionKey() {
        return selectionKey;
    }


    public ActivityHandle addIOWatch(IOWatchMode mode, Runnable notifyAction) {
        long watchSeqId = watchSeqIdGenerator.generateNext();
        Watch watch = new Watch(watchSeqId, mode, notifyAction);
        TreeSet<Watch> matchingWatchSet = watches.computeIfAbsent(mode, k -> new TreeSet<Watch>());
        matchingWatchSet.add(watch);
        updateSelectionKey();

        return new ActivityHandleImpl(watch);
    }


    public void handleSelection() {
        int readyOps = selectionKey.readyOps();
        ArrayList<Watch> readyWatches = new ArrayList<Watch>();
        for (Map.Entry<IOWatchMode, TreeSet<Watch>> entry : watches.entrySet()) {
            if ((entry.getKey().getSelectionOp() & readyOps) != 0) {
                readyWatches.addAll(entry.getValue());
            }
        }

        for (Watch watch : readyWatches) {
            TreeSet<Watch> matchingWatchSet = watches.get(watch.getMode());
            if (matchingWatchSet != null && matchingWatchSet.contains(watch)) {
                watch.runNotifyAction();
            }
        }
    }


    private void updateSelectionKey() {
        int selectionOps = 0;
        for (Map.Entry<IOWatchMode, TreeSet<Watch>> entry : watches.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                selectionOps |= entry.getKey().getSelectionOp();
            }
        }

        selectionKey.interestOps(selectionOps);
        if (selectionOps == 0) {
            clearedListener.run();
        }
    }



    private final class ActivityHandleImpl implements ActivityHandle {

        private final Watch watch;


        public ActivityHandleImpl(Watch watch) {
            this.watch = watch;
        }


        @Override
        public boolean isActive() {
            TreeSet<Watch> matchingWatchSet = watches.get(watch.getMode());
            return matchingWatchSet != null && matchingWatchSet.contains(watch);
        }


        @Override
        public void deactivate() {
            TreeSet<Watch> matchingWatchSet = watches.get(watch.getMode());
            if (matchingWatchSet == null || !matchingWatchSet.remove(watch)) {
                throw new IllegalStateException("The watch is already inactive");
            }
            updateSelectionKey();
        }
    }



    private static final class Watch extends EqualsComparable<Watch> implements Comparable<Watch> {

        private final long seqId;
        private final IOWatchMode mode;
        private final Runnable notifyAction;


        public Watch(long seqId, IOWatchMode mode, Runnable notifyAction) {
            this.seqId = seqId;
            this.mode = mode;
            this.notifyAction = notifyAction;
        }


        @Override
        public int compareTo(Watch other) {
            return Long.compare(seqId, other.seqId);
        }


        @Override
        protected Class<Watch> getEqualsClass() {
            return Watch.class;
        }


        @Override
        public boolean equalsImpl(Watch other) {
            return seqId == other.seqId;
        }


        @Override
        public int hashCodeImpl() {
            return (int) seqId;
        }


        public IOWatchMode getMode() {
            return mode;
        }


        public void runNotifyAction() {
            notifyAction.run();
        }
    }
}
