package com.flow.eda.runner.flow.node.timer;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicInteger;

/** 定时任务，用于执行定时器节点的任务 */
public class TimerTask {

    /** 定时执行任务，不限次数 */
    public static void run(TimerNode task) {
        Trigger trigger = getTrigger(task);
        getThreadPool().schedule(task.getRunnable(), trigger);
    }

    /** 定时执行指定次数后停止，并进行回调（通知节点已经执行完毕） */
    public static void runTimes(TimerNode task, Runnable callback) {
        // 需要执行的次数
        int times = task.getTimes();
        AtomicInteger n = new AtomicInteger();
        ThreadPoolTaskScheduler executor = getThreadPool();
        Runnable command =
                () -> {
                    if (n.getAndIncrement() < times) {
                        // 最后一次执行前需要回调
                        if (n.get() == times) {
                            callback.run();
                        }
                        task.getRunnable().run();
                    } else {
                        // 停止当前定时线程
                        executor.shutdown();
                    }
                };
        if (task.getCron() != null) {
            executor.schedule(command, getTrigger(task));
        } else {
            executor.getScheduledExecutor()
                    .scheduleAtFixedRate(command, 0, task.getPeriod(), task.getUnit());
        }
    }

    /** 为每个节点实例单独创建定时线程池 */
    private static ThreadPoolTaskScheduler getThreadPool() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(1);
        threadPoolTaskScheduler.setThreadNamePrefix("scheduledTask-");
        threadPoolTaskScheduler.initialize();
        return threadPoolTaskScheduler;
    }

    private static Trigger getTrigger(TimerNode task) {
        Trigger trigger;
        if (task.getCron() != null) {
            trigger = new CronTrigger(task.getCron(), ZoneId.systemDefault());
        } else {
            trigger = new PeriodicTrigger(task.getPeriod(), task.getUnit());
        }
        return trigger;
    }
}
