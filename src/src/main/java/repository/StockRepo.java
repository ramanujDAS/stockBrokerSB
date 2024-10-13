package repository;

import exception.OrderNotFoundException;
import exception.UserNotFoundException;
import model.OrderStatus;
import model.StockSymbol;
import repository.model.OrderDao;
import repository.model.TradeDao;
import repository.model.UserDao;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class StockRepo implements  IStockRepo{

    private final CopyOnWriteArrayList<OrderDao> orderBook;
    private final CopyOnWriteArrayList<TradeDao> tradeHistory ;
    private final HashMap<Long,UserDao>  users ;

    public StockRepo() {
        this.orderBook = new CopyOnWriteArrayList<>();
        this.tradeHistory = new CopyOnWriteArrayList<>();
        this.users = new HashMap<>();
    }

    @Override
    public List<OrderDao> getOrderBook() {
        return Collections.unmodifiableList(orderBook);
    }

    @Override
    public List<TradeDao> getTradeHistory() {
        return Collections.unmodifiableList(tradeHistory);
    }

    @Override
    public boolean addOrder(OrderDao order) {
        return orderBook.add(order);
    }

    @Override
    public boolean removeOrder(long orderId) {
        return orderBook.remove(orderId);
    }

    @Override
    public boolean addTrade(TradeDao trade) {
        return tradeHistory.add(trade);
    }

    @Override
    public boolean removeTrade(TradeDao trade) {
        return tradeHistory.remove(trade);
    }

    @Override
    public OrderStatus getOrderStatus(long orderId) {
        for(OrderDao order : orderBook){
            if(order.getOrderId() == orderId){
                return order.getStatus();
            }
        }
        throw new OrderNotFoundException("order not found for orderId = " + orderId);
    }

    @Override
    public boolean modifyOrder(OrderDao orderDao) {
        for(OrderDao order : orderBook){
            if(order.getOrderId() == orderDao.getOrderId()){
                order.setQuantity(orderDao.getQuantity());
                order.setPrice(orderDao.getPrice());
                order.setStatus(orderDao.getStatus());
                System.out.println("order modified successfully for orderId = " + orderDao.getOrderId());
                return true;
            }
        }
        System.out.println("modifyOrder :: order not found in pending for orderId = " + orderDao.getOrderId());
        return false;
    }

    @Override
    public boolean cancelOrder(long orderId) {
        for(OrderDao order : orderBook){
            if(order.getOrderId() == orderId){
                order.setStatus(OrderStatus.CANCELLED);
                System.out.println("order cancelled successfully for orderId = " + orderId);
                return true;
            }
        }

        System.out.println("cancelOrder :: order not found in pending for orderId = " + orderId);
        return false;
    }

    @Override
    public boolean updateOrder(OrderDao order) {

          for(OrderDao orderDao : orderBook){
              if (orderDao.equals(order) && orderDao.getStatus() == OrderStatus.PENDING) {
                  orderDao.setStatus(order.getStatus());
                  orderDao.setQuantity(order.getQuantity());
                  if (order.getQuantity() == 0) {
                      orderDao.setStatus(OrderStatus.ACCEPTED);
                  }
                  return true;
              }
          }
        System.out.println("updateOrder :: order not found for orderId = " + order.getOrderId());
          return false;
    }

    @Override
    public boolean insertUser(UserDao user) {
        users.put(user.getUserId(),user);
        return true;
    }

    @Override
    public UserDao getUser(long userId) {
        UserDao user = users.get(userId);
        if (user == null) {
            System.out.println("user not found for userId = " + userId);
            throw new UserNotFoundException();
        }
        return user;

    }

    @Override
    public List<OrderDao> getOrdersByUser(long userId) {
        List<OrderDao> pendingOrders = orderBook.stream()
                .filter(order -> order.getUserId() == userId)
                .collect(Collectors.toList());

        if (!pendingOrders.isEmpty()) {
            return pendingOrders;
        }

        System.out.println("no order found for userId = " + userId);
        return Collections.emptyList();
    }

    @Override
    public List<OrderDao> getUnExecutedOrders(StockSymbol symbol) {
        List<OrderDao> pendingOrders =  orderBook.stream()
                .filter(order -> symbol.equals(order.getStockSymbol()) && order.getStatus() == OrderStatus.PENDING)
                .collect(Collectors.toList());

        if(!pendingOrders.isEmpty()){
            return pendingOrders;
        }
        System.out.println("no pending order found for userId");
        return Collections.emptyList();
    }

    @Override
    public OrderDao getOrder(long orderId) {
        Optional<OrderDao> order = orderBook.stream()
                .filter(o -> o.getOrderId() == orderId)
                .findFirst();

        if (!order.isPresent()) {
            System.out.println("no order found with given orderId = " + orderId);
            return null;
        }
        return order.get();
    }

}
