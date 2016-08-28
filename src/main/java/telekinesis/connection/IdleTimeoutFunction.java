package telekinesis.connection;

import io.netty.channel.EventLoop;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.concurrent.TimeUnit;

public abstract class IdleTimeoutFunction {

    private final EventLoop eventLoop;
    private long timeoutNanos;
    private long lastPing;
    private ScheduledFuture<?> timeoutFunc;

    public IdleTimeoutFunction(EventLoop eventLoop) {
        this.eventLoop = eventLoop;
        this.lastPing = 0L;
    }

    public void enable(int timeoutSeconds) {
        disable();
        this.timeoutNanos = (long) timeoutSeconds * 1000000000L;
        schedule(new TimeoutFunction(), timeoutNanos);
    }

    public void disable() {
        if (timeoutFunc != null) {
            timeoutFunc.cancel(false);
            timeoutFunc = null;
        }
    }

    public void resetTimer() {
        lastPing = System.nanoTime();
    }

    protected abstract void onTimout();

    private void schedule(TimeoutFunction function, long delay) {
        timeoutFunc = eventLoop.schedule(function, delay, TimeUnit.NANOSECONDS);
    }

    private class TimeoutFunction implements Runnable {
        @Override
        public void run() {
            long nextDelay = timeoutNanos - (System.nanoTime() - lastPing);
            if (nextDelay <= 0) {
                // Writer is idle - set a new timeout and notify the callback.
                schedule(this, timeoutNanos);
                onTimout();
            } else {
                // Write occurred before the timeout - set a new timeout with shorter delay.
                schedule(this, nextDelay);
            }
        }
    }

}
