/*
 * Copyright (C) 2018,2020, Zbigniew Chyla
 *
 * This file is part of jloop library. See LICENSE file for licensing information.
 */
package pl.chyla.jloop.core;


public final class BasicEventLoop implements EventLoop {

    private final TasksScheduler tasksScheduler;
    private long time;


    BasicEventLoop() {
        tasksScheduler = new TasksScheduler();
        time = MonotonicClock.getTime();
    }


    public void processTimeUpdate(long newTime) {
        if (newTime < time) {
            throw new IllegalStateException("Can't move backward in time");
        }

        time = newTime;

        while (tasksScheduler.hasScheduledTasks() && tasksScheduler.getEarliestScheduleTime() <= time) {
            Runnable task = tasksScheduler.pullNextScheduledTask();
            task.run();
        }
    }


    public long getNextWakeupTimeOrMax() {
        return tasksScheduler.hasScheduledTasks() ? tasksScheduler.getEarliestScheduleTime() : Long.MAX_VALUE;
    }


    @Override
    public ActivityHandle invokeAt(long time, Runnable task) {
        return tasksScheduler.scheduleTask(time, task);
    }


    @Override
    public long getTime() {
        return time;
    }
}
