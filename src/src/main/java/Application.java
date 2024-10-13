import counter.OrderCounter;
import model.OrderStatus;
import model.OrderType;
import model.StockSymbol;
import model.User;
import repository.StockRepo;
import service.BuyOrderService;
import service.model.Order;
import service.OrderService;
import service.mapper.Converter;
import service.ordermatch.service.OldestOrder;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Application {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Application Started!");
        StockRepo stockRepo = new StockRepo();
        Converter converter = new Converter();

        //dummy sell order
        Order sellOrder = new Order(OrderCounter.getOrderCounter(), 1, OrderType.SELL, StockSymbol.PAYTM, 10, 104, OrderStatus.PENDING, System.currentTimeMillis());
        Order sellOrder1 = new Order(OrderCounter.getOrderCounter(), 1, OrderType.SELL, StockSymbol.PAYTM, 10, 106, OrderStatus.PENDING, System.currentTimeMillis());

       //1. A registered user can place, modify, and cancel his orders.

        User user = new User(1, "user1", "9831633512");
        stockRepo.insertUser(converter.getUserDao(user));

        OrderService orderService = new BuyOrderService(new OldestOrder(), stockRepo, converter);
        orderService.addOrder(sellOrder);
        orderService.addOrder(sellOrder1);

        Thread.sleep(1000);
        Order buyOrder = new Order(OrderCounter.getOrderCounter(), 2, OrderType.BUY, StockSymbol.PAYTM, 10, 103, OrderStatus.PENDING, System.currentTimeMillis());
        orderService.executeOrder(buyOrder);
        System.out.println("final order book =" + stockRepo.getOrderBook());
        System.out.println();
        System.out.println("tradeHistory = " + stockRepo.getTradeHistory());

        //1.1 modify order with new price and Quantity
        orderService.modifyOrder(2, 2, 105, 100);
        System.out.println("order book after modifying order = " + stockRepo.getOrder(2));

        //1.2 cancel order
        orderService.cancelOrder(2, 2);
        System.out.println();
        System.out.println("order book after canceling order = " + stockRepo.getOrder(2));


        //2. A user should be able to query the status of his order
        long userId = user.getUserId();
        System.out.println(orderService.getOrdersByUser(userId));

        //3.The system should be able to execute trades based on matching buy
        //and sell orders. A trade is executed when the buy and sell price of two different orders
        //match. (Buy price greater than or equal to Sell Price). If multiple eligible orders can be
        //matched with the same price, match the oldest orders first.

        Thread.sleep(1000);
        Order buyOrder1 = new Order(OrderCounter.getOrderCounter(), 2, OrderType.BUY, StockSymbol.PAYTM, 10, 110, OrderStatus.PENDING, System.currentTimeMillis());
        orderService.executeOrder(buyOrder1);

        System.out.println("order book after executing order = " + stockRepo.getOrderBook());
        System.out.println("tradeHistory = " + stockRepo.getTradeHistory());

        //4.Concurrent order placement, modification, cancellation, and execution
        //should be handled appropriately.

        //dummmy sell order
        Order sellOrder2 = new Order(OrderCounter.getOrderCounter(), 1, OrderType.SELL, StockSymbol.PAYTM, 10, 104, OrderStatus.PENDING, System.currentTimeMillis());
        Order sellOrder3 = new Order(OrderCounter.getOrderCounter(), 1, OrderType.SELL, StockSymbol.PAYTM, 10, 106, OrderStatus.PENDING, System.currentTimeMillis());

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        StockRepo stockRepoExecutor = new StockRepo();
        Converter converterExecutor = new Converter();
        OrderService orderServiceExecutor = new BuyOrderService(new OldestOrder(),stockRepoExecutor, converterExecutor);


        orderServiceExecutor.addOrder(sellOrder2);
        orderServiceExecutor.addOrder(sellOrder3);

        executorService.execute(() -> {
            Order buyOrder3 = new Order(OrderCounter.getOrderCounter(), 2, OrderType.BUY, StockSymbol.PAYTM, 10, 110, OrderStatus.PENDING, System.currentTimeMillis());
            orderServiceExecutor.executeOrder(buyOrder3);
        });
        executorService.execute(() -> {
            Order buyOrder4 = new Order(OrderCounter.getOrderCounter(), 2, OrderType.BUY, StockSymbol.PAYTM, 10, 109, OrderStatus.PENDING, System.currentTimeMillis());
            orderServiceExecutor.executeOrder(buyOrder4);
        });
        executorService.execute(() -> {
            Order buyOrder5 = new Order(OrderCounter.getOrderCounter(), 2, OrderType.BUY, StockSymbol.PAYTM, 10, 111, OrderStatus.PENDING, System.currentTimeMillis());
            orderServiceExecutor.executeOrder(buyOrder5);
        });
        executorService.execute(() -> {
            Order buyOrder6 = new Order(OrderCounter.getOrderCounter(), 2, OrderType.BUY, StockSymbol.PAYTM, 10, 108, OrderStatus.PENDING, System.currentTimeMillis());
            orderServiceExecutor.executeOrder(buyOrder6);
        });

        Thread.sleep(1000); // waiting for last thread to execute before shutting it down
        executorService.shutdown();

        System.out.println();
        System.out.println(" Executor Service = order book after executing order = " + stockRepoExecutor.getOrderBook());
        System.out.println(" Executor Service = tradeHistory = " + stockRepoExecutor.getTradeHistory());


       // 5.The system should maintain an order book per symbol, which holds all the current
       // unexecuted orders.

        System.out.println("Unexecuted Orders by StockSymbol =" + orderService.getUnexecutedOrders(StockSymbol.PAYTM));


    // additional feature  = timed expiry for orders
        Order orderWithExpiry = new Order(25, 1, OrderType.SELL, StockSymbol.ADANI, 10, 104, OrderStatus.PENDING, System.currentTimeMillis());
        orderWithExpiry.setOrderExpiryInMinutes(1L);
        orderService.addOrder(orderWithExpiry);
        Thread.sleep(Duration.ofMinutes(2).toMillis());
        System.out.println(orderService.getOrderStatus(25));




    }
}
