package repository;

import model.OrderStatus;
import model.StockSymbol;
import repository.model.OrderDao;
import repository.model.TradeDao;
import repository.model.UserDao;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
/** Interface for Stock Repository
 *  individual repository should implement this
 *  */

public interface IStockRepo{


     List<OrderDao> getOrderBook();
     List<TradeDao> getTradeHistory();
     boolean addOrder(OrderDao order);
     boolean removeOrder(long orderId);
     boolean addTrade(TradeDao trade);
     boolean removeTrade(TradeDao trade);
     OrderStatus getOrderStatus(long orderId);
     boolean modifyOrder(OrderDao order );
     boolean cancelOrder(long orderId);
     boolean updateOrder(OrderDao order);
     boolean insertUser(UserDao user);
     UserDao getUser(long userId);
     List<OrderDao> getOrdersByUser(long userId);
     List<OrderDao>   getUnExecutedOrders(StockSymbol symbol);
     OrderDao getOrder(long orderId);

}
