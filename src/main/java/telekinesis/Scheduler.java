package telekinesis;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import telekinesis.event.Event;

public class Scheduler {

    private static final Set<SteamClient> steamClients = new HashSet<SteamClient>(); 

    private static ScheduledThreadPoolExecutor executor;
    
    public static void registerSteamClient(final SteamClient clientToAdd) {
        steamClients.add(clientToAdd);
        Event.register(clientToAdd, new SteamClient.PRE_DESTROY() {
            @Override
            public void handle(SteamClient clientToRemove) {
                Scheduler.steamClients.remove(clientToRemove);
                if (Scheduler.steamClients.size() == 0 && executor != null) {
                    executor().shutdownNow();
                    executor = null;
                }
            }
        });
    }
    
    private static ScheduledThreadPoolExecutor executor() {
        if (executor == null) {
            executor = new ScheduledThreadPoolExecutor(1);
        }
        return executor;
    }

    public static <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return executor().invokeAny(tasks);
    }

    public static <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return executor().invokeAny(tasks, timeout, unit);
    }

    public static <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return executor().invokeAll(tasks);
    }

    public static <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return executor().invokeAll(tasks, timeout, unit);
    }

    public static ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return executor().schedule(command, delay, unit);
    }

    public static <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return executor().schedule(callable, delay, unit);
    }

    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return executor().scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return executor().scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }

    public static void execute(Runnable command) {
        executor().execute(command);
    }

    public static Future<?> submit(Runnable task) {
        return executor().submit(task);
    }

    public static <T> Future<T> submit(Runnable task, T result) {
        return executor().submit(task, result);
    }

    public static <T> Future<T> submit(Callable<T> task) {
        return executor().submit(task);
    }

    public static void setContinueExistingPeriodicTasksAfterShutdownPolicy(boolean value) {
        executor().setContinueExistingPeriodicTasksAfterShutdownPolicy(value);
    }

    public static boolean getContinueExistingPeriodicTasksAfterShutdownPolicy() {
        return executor().getContinueExistingPeriodicTasksAfterShutdownPolicy();
    }

    public static void setExecuteExistingDelayedTasksAfterShutdownPolicy(boolean value) {
        executor().setExecuteExistingDelayedTasksAfterShutdownPolicy(value);
    }

    public static boolean getExecuteExistingDelayedTasksAfterShutdownPolicy() {
        return executor().getExecuteExistingDelayedTasksAfterShutdownPolicy();
    }

    public static void setRemoveOnCancelPolicy(boolean value) {
        executor().setRemoveOnCancelPolicy(value);
    }

    public boolean getRemoveOnCancelPolicy() {
        return executor().getRemoveOnCancelPolicy();
    }

    public static void shutdown() {
        executor().shutdown();
    }

    public static List<Runnable> shutdownNow() {
        return executor().shutdownNow();
    }

    public static BlockingQueue<Runnable> getQueue() {
        return executor().getQueue();
    }

    public static boolean isShutdown() {
        return executor().isShutdown();
    }

    public static boolean isTerminating() {
        return executor().isTerminating();
    }

    public static boolean isTerminated() {
        return executor().isTerminated();
    }

    public static boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return executor().awaitTermination(timeout, unit);
    }

    public static void setThreadFactory(ThreadFactory threadFactory) {
        executor().setThreadFactory(threadFactory);
    }

    public static ThreadFactory getThreadFactory() {
        return executor().getThreadFactory();
    }

    public static void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
        executor().setRejectedExecutionHandler(handler);
    }

    public static RejectedExecutionHandler getRejectedExecutionHandler() {
        return executor().getRejectedExecutionHandler();
    }

    public static void setCorePoolSize(int corePoolSize) {
        executor().setCorePoolSize(corePoolSize);
    }

    public static int getCorePoolSize() {
        return executor().getCorePoolSize();
    }

    public static boolean prestartCoreThread() {
        return executor().prestartCoreThread();
    }

    public static int prestartAllCoreThreads() {
        return executor().prestartAllCoreThreads();
    }

    public static boolean allowsCoreThreadTimeOut() {
        return executor().allowsCoreThreadTimeOut();
    }

    public static void allowCoreThreadTimeOut(boolean value) {
        executor().allowCoreThreadTimeOut(value);
    }

    public static void setMaximumPoolSize(int maximumPoolSize) {
        executor().setMaximumPoolSize(maximumPoolSize);
    }

    public static int getMaximumPoolSize() {
        return executor().getMaximumPoolSize();
    }

    public static void setKeepAliveTime(long time, TimeUnit unit) {
        executor().setKeepAliveTime(time, unit);
    }

    public static long getKeepAliveTime(TimeUnit unit) {
        return executor().getKeepAliveTime(unit);
    }

    public static boolean remove(Runnable task) {
        return executor().remove(task);
    }

    public static void purge() {
        executor().purge();
    }

    public static int getPoolSize() {
        return executor().getPoolSize();
    }

    public static int getActiveCount() {
        return executor().getActiveCount();
    }

    public static int getLargestPoolSize() {
        return executor().getLargestPoolSize();
    }

    public static long getTaskCount() {
        return executor().getTaskCount();
    }

    public static long getCompletedTaskCount() {
        return executor().getCompletedTaskCount();
    }

}
