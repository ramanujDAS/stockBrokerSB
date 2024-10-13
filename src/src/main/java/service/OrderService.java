package service;

import counter.TradeCounter;
import exception.DBOperationException;
import exception.TransactionException;
import model.OrderStatus;
import model.StockSymbol;
import repository.IStockRepo;
import repository.TradeType;
import repository.model.OrderDao;
import repository.model.TradeDao;
import service.mapper.Converter;
import service.model.Order;
import service.model.Trade;
import service.ordermatch.service.IMatchingStrategy;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class OrderService {
    protected IMatchingStrategy matchingStrategy;
    private final IStockRepo stockRepo;
    private final Converter converter;
    private final Lock orderLock;

    public OrderService(IMatchingStrategy matchingStrategy , IStockRepo stockRepo , Converter converter) {
        this.matchingStrategy = matchingStrategy;
        this.stockRepo = stockRepo;
        this.converter = converter;
        orderLock = new ReentrantLock();
    }

    public abstract boolean executeOrder(Order order);

    public OrderStatus getOrderStatus(long orderId) {
        OrderDao order = stockRepo.getOrder(orderId);
        if (Objects.nonNull(order) && Objects.nonNull(order.getOrderExpiryInMinutes()) && isExpired(order.getOrderId())) {
            order.setStatus(OrderStatus.CANCELLED);
           // stockRepo.updateOrder(order);
        }
        return stockRepo.getOrderStatus(orderId);
    }

    public boolean modifyOrder(long userId,long orderId , int newQuantity , long newPrice  ) {

        OrderDao order = stockRepo.getOrder(orderId);

        if (Objects.isNull(order)) {
            System.out.println("order not found");
            return false;
        }
        order.setPrice(newPrice);
        order.setQuantity(newQuantity);

        if (order.getUserId() != userId) {
            throw new TransactionException("user not authorized to modify order");
        }
        return modifyOrder(order);
    }

    private boolean modifyOrder(OrderDao orderDao) {
        orderLock.lock();
        try {
            OrderDao order = stockRepo.getOrder(orderDao.getOrderId());
            if (!order.getStatus().equals(OrderStatus.PENDING)) {
                System.out.println("cannot modify order as it is executed");
                return false;
            }
            return stockRepo.modifyOrder(orderDao);
        } finally {
            orderLock.unlock();
        }
    }

    public boolean cancelOrder(long orderId , long userId) {

        OrderDao order = stockRepo.getOrder(orderId);
        if(Objects.isNull(order)){
            System.out.println("order not found");
            return false;
        }
        if (!order.getStatus().equals(OrderStatus.PENDING)) {
            System.out.println("cannot modify order as it is executed");
            return false;
        }
        order.setStatus(OrderStatus.CANCELLED);
        if (order.getUserId() != userId) {
            System.out.println("user not authorized to cancel order");
            return false;
        }

        return modifyOrder(order);
    }

    public boolean addOrder(Order order) {
        return stockRepo.addOrder(converter.getOrderDao(order));  // mapper
    }

    public boolean removeOrder(Order order) {
        return stockRepo.removeOrder(order.getOrderId());
    }

    public List<Order> getOrderBook() {
        List<Order> orders = new CopyOnWriteArrayList<>();
          for(OrderDao order : stockRepo.getOrderBook()){
              orders.add(converter.getOrder(order));
          }
          return orders;
    }

    public List<Trade> getTradeHistory() {
        List<Trade> trades = new CopyOnWriteArrayList<>();
        for(TradeDao trade : stockRepo.getTradeHistory()){
            trades.add(converter.getTrade(trade));
        }
        return trades;

    }

    private boolean addTrade(Trade trade) {
        return stockRepo.addTrade(converter.getTradeDao(trade)); // mapper

    }

    private boolean updateOrder(Order order) {
        return stockRepo.updateOrder(converter.getOrderDao(order));
    }

    public void updateOnSuccess(Order placedOrder, Order order, TradeType tradeType) {
        orderLock.lock();
        try {
            if (getOrderStatus(order.getOrderId()) != OrderStatus.PENDING) {
                System.out.println("order is already processed for orderId = " + order.getOrderId() + " so rejecting this order");
                throw new TransactionException("order is already processed for orderId = " + order.getOrderId());
            }
            Trade trade = new Trade(TradeCounter.getTradeCounter(), tradeType, placedOrder.getOrderId(), order.getOrderId(), placedOrder.getStockSymbol(), placedOrder.getQuantity(), placedOrder.getPrice(), System.currentTimeMillis());
            addTrade(trade);
            order.setStatus(OrderStatus.ACCEPTED);
            placedOrder.setStatus(OrderStatus.ACCEPTED);
            addOrder(placedOrder);
            updateOrder(order);
        } catch (TransactionException e) {
            System.out.println("got some  error while processing orderId =  " + placedOrder.getOrderId() + e.getMessage());
            throw new TransactionException("some issue while executing the order");
        } catch (Exception e) {
            System.out.println("got some un handled error while processing orderId =  " + placedOrder.getOrderId());
            throw new DBOperationException();
        } finally {
            orderLock.unlock();
        }
    }

    public boolean isExpired(long orderId) {
        OrderDao order = stockRepo.getOrder(orderId);
        //can be used with zoneid as well for different timezones for accuracy
        Instant timeStamp = Instant.ofEpochMilli(order.getTimeStamp());
        long timeDifference = Duration.between(timeStamp, Instant.now()).toMinutes();
        return order.getOrderExpiryInMinutes() < timeDifference;
    }

    public List<Order> getUnexecutedOrders(StockSymbol symbol) {
        List<Order> orders = new CopyOnWriteArrayList<>();
        for (OrderDao order : stockRepo.getUnExecutedOrders(symbol)) {
            orders.add(converter.getOrder(order));
        }
        return orders;
    }
   public List<Order> getOrdersByUser(long userId) {
       List<Order> orders = new CopyOnWriteArrayList<>();
       for (OrderDao order : stockRepo.getOrdersByUser(userId)) {
           orders.add(converter.getOrder(order));
       }
       return orders;
   }
}
