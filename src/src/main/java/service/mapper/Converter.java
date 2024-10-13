package service.mapper;

import model.User;
import repository.model.OrderDao;
import repository.model.TradeDao;
import repository.model.UserDao;
import service.model.Order;
import service.model.Trade;

public class Converter {

    public Order getOrder(OrderDao orderDao) {
        Order order = new Order(orderDao.getOrderId(),
                orderDao.getUserId(),
                orderDao.getOrderType(),
                orderDao.getStockSymbol(),
                orderDao.getQuantity(),
                orderDao.getPrice(),
                orderDao.getStatus(),
                orderDao.getTimeStamp()
        );
        order.setOrderExpiryInMinutes(orderDao.getOrderExpiryInMinutes());
        return order;
    }

    public Trade getTrade(TradeDao tradeDao) {
        return new Trade(tradeDao.getTradeId(),
                tradeDao.getTradeType(),
                tradeDao.getBuyerOrderId(),
                tradeDao.getSellerOrderId(),
                tradeDao.getStockSymbol(),
                tradeDao.getQuantity(),
                tradeDao.getPrice(),
                tradeDao.getTimeStamp()
        );
    }

    public OrderDao getOrderDao(Order order) {
        OrderDao orderDao = new OrderDao(order.getOrderId(),
                order.getUserId(),
                order.getOrderType(),
                order.getStockSymbol(),
                order.getQuantity(),
                order.getPrice(),
                order.getStatus(),
                order.getTimeStamp()
        );
        orderDao.setOrderExpiryInMinutes(order.getOrderExpiryInMinutes());
        return orderDao;
    }

    public TradeDao getTradeDao(Trade trade) {
        return new TradeDao(trade.getTradeId(),
                trade.getTradeType(),
                trade.getBuyerOrderId(),
                trade.getSellerOrderId(),
                trade.getStockSymbol(),
                trade.getQuantity(),
                trade.getPrice(),
                trade.getTimeStamp()
        );
    }
    public UserDao getUserDao(User user) {
        return new UserDao(user.getUserId(),
                user.getUserName(),
                user.getPhoneNumber(),
                user.getEmailId()
        );
    }
}
