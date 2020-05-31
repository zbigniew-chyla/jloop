/*
 * Copyright (C) 2018,2020, Zbigniew Chyla
 *
 * This file is part of jloop library. See LICENSE file for licensing information.
 */
package pl.chyla.jloop.core;

import java.util.Map.Entry;
import java.util.TreeMap;
import pl.chyla.jloop.utils.SeqIdGenerator;


final class TasksScheduler {

    private final TreeMap<TaskKey, Runnable> scheduledTasks;
    private final SeqIdGenerator taskKeySeqIdGenerator;


    public TasksScheduler() {
        scheduledTasks = new TreeMap<TaskKey, Runnable>();
        taskKeySeqIdGenerator = new SeqIdGenerator();
    }


    public ActivityHandle scheduleTask(long time, Runnable task) {
        TaskKey taskKey = new TaskKey(time, taskKeySeqIdGenerator.generateNext());
        scheduledTasks.put(taskKey, task);
        return new TaskActivityHandle(taskKey);
    }


    public long getEarliestScheduleTime() {
        if (scheduledTasks.isEmpty()) {
            throw new IllegalStateException("No scheduled tasks");
        }
        return scheduledTasks.firstEntry().getKey().time;
    }


    public boolean hasScheduledTasks() {
        return !scheduledTasks.isEmpty();
    }


    public Runnable pullNextScheduledTask() {
        Entry<TaskKey, Runnable> taskEntry = scheduledTasks.pollFirstEntry();
        if (taskEntry == null) {
            throw new IllegalStateException("No scheduled tasks");
        }
        return taskEntry.getValue();
    }



    public static final class TaskKey implements Comparable<TaskKey> {

        private final long time;
        private final long seqId;


        private TaskKey(long time, long seqId) {
            this.time = time;
            this.seqId = seqId;
        }


        @Override
        public int compareTo(TaskKey other) {
            return time < other.time || (time == other.time && seqId < other.seqId)
                ? -1
                : time == other.time && seqId == other.seqId
                    ? 0
                    : 1;
        }
    }



    private final class TaskActivityHandle implements ActivityHandle {

        private final TaskKey taskKey;


        public TaskActivityHandle(TaskKey taskKey) {
            this.taskKey = taskKey;
        }


        @Override
        public boolean isActive() {
            return scheduledTasks.containsKey(taskKey);
        }


        @Override
        public void deactivate() {
            Runnable removedTask = scheduledTasks.remove(taskKey);
            if (removedTask == null) {
                throw new IllegalStateException("Trying to deschedule non-existing task");
            }
        }
    }
}
