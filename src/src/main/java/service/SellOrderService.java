package service;

import exception.TransactionException;
import model.OrderStatus;
import model.OrderType;
import repository.IStockRepo;
import repository.TradeType;
import service.mapper.Converter;
import service.model.Order;
import service.ordermatch.service.IMatchingStrategy;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class SellOrderService extends OrderService {

    public SellOrderService(IMatchingStrategy matchingStrategy , IStockRepo stockRepo , Converter converter) {
        super(matchingStrategy , stockRepo, converter);
    }

    @Override
    public boolean executeOrder(Order placedOrder) {
        return process(placedOrder);
    }
    public boolean process(Order placedOrder) {
        List<Order> matchedOrders = new CopyOnWriteArrayList<>();

        try{
            for(Order buyOrder : getOrderBook()) {
                if (buyOrder.getStockSymbol() == placedOrder.getStockSymbol() &&
                        buyOrder.getStatus() == OrderStatus.PENDING &&
                        OrderType.BUY == buyOrder.getOrderType() &&
                        buyOrder.getQuantity() == placedOrder.getQuantity() &&
                        buyOrder.getPrice() >= placedOrder.getPrice()) {
                    //for expiry check orders
                    if(Objects.nonNull(buyOrder.getOrderExpiryInMinutes()) && isExpired(buyOrder.getOrderId())) continue;

                    matchedOrders.add(buyOrder);

                }
            }
            if (matchedOrders.isEmpty()) {
                System.out.println("order not executed successfully for order Id = " + placedOrder.getOrderId());
                placedOrder.setStatus(OrderStatus.REJECTED);
                addOrder(placedOrder);
                return false;
            }
                Optional<Order> transactionOrder =  matchingStrategy.match(matchedOrders);

                if(!transactionOrder.isPresent()){
                    System.out.println("order not executed for orderID = " +placedOrder.getOrderId());
                }
                Order order = transactionOrder.get();
                placedOrder.setQuantity(placedOrder.getQuantity() - order.getQuantity());
                updateOnSuccess(placedOrder,order,TradeType.SELL);

                System.out.println("order successfully sold for orderId = " + placedOrder.getOrderId());

        }catch (TransactionException e) {
            System.out.println("got some error while processing orderId =  " + placedOrder.getOrderId() + e.getMessage());
            return false;
        }
        catch (Exception e) {
            System.out.println("got some un handled error while processing orderId =  " + placedOrder.getOrderId() + e.getMessage());
            return false;
        }
        return true;

    }
}
