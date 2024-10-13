package counter;

import java.util.concurrent.atomic.AtomicLong;

public class TradeCounter {
    private static AtomicLong tradeCounter = new AtomicLong(-1);

    public  static long getTradeCounter() {
        return tradeCounter.incrementAndGet();
    }
}
