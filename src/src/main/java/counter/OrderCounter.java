package counter;

import java.util.concurrent.atomic.AtomicLong;

public class OrderCounter {
    private static AtomicLong orderCounter = new AtomicLong(-1);

    public  static long getOrderCounter() {
        return orderCounter.incrementAndGet();
    }
}
