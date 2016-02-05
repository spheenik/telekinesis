package telekinesis.connection;

import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.concurrent.TimeUnit;

public abstract class IdleTimeoutFunction {

    private final EventLoopGroup eventLoopGroup;
    private final long timeoutNanos;
    private long lastPing;
    private ScheduledFuture<?> timeoutFunc;

    public IdleTimeoutFunction(EventLoopGroup eventLoopGroup, int timeoutSeconds) {
        this.eventLoopGroup = eventLoopGroup;
        this.timeoutNanos = (long) timeoutSeconds * 1000000000L;
        this.lastPing = 0L;
        schedule(new TimeoutFunction(), timeoutNanos);
    }

    public void reset() {
        lastPing = System.nanoTime();
    }

    public void cancel() {
        timeoutFunc.cancel(false);
    }

    protected abstract void onTimout();

    private void schedule(TimeoutFunction function, long delay) {
        timeoutFunc = eventLoopGroup.schedule(function, delay, TimeUnit.NANOSECONDS);
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
