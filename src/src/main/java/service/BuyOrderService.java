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
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class BuyOrderService extends OrderService {

    public BuyOrderService(IMatchingStrategy matchingStrategy , IStockRepo stockRepo , Converter converter) {
        super(matchingStrategy,stockRepo,converter);
    }

    @Override
    public boolean executeOrder(Order placedOrder) {
        return process(placedOrder);
    }


   private boolean process(Order placedOrder) {
        List<Order> matchedOrders = new CopyOnWriteArrayList<>();

        try{
            for(Order sellOrder : getOrderBook()) {
                if (sellOrder.getStockSymbol() == placedOrder.getStockSymbol() &&
                        sellOrder.getStatus() == OrderStatus.PENDING &&
                        OrderType.SELL == sellOrder.getOrderType() &&
                        sellOrder.getQuantity() == placedOrder.getQuantity() &&
                        sellOrder.getPrice() <= placedOrder.getPrice()) {

                    //for expiry check orders
                    if(sellOrder.getOrderExpiryInMinutes() != null && isExpired(sellOrder.getOrderId())) continue;

                    matchedOrders.add(sellOrder);

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
              order.setQuantity(order.getQuantity() - placedOrder.getQuantity());
             updateOnSuccess(placedOrder,order,TradeType.BUY);

             System.out.println("order successfully bought for orderId = " + placedOrder.getOrderId());

        } catch (TransactionException e) {
            System.out.println("got some error while processing orderId =  " + placedOrder.getOrderId() + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("got some un handled error while processing orderId =  " + placedOrder.getOrderId() + e.getMessage());
            return false;
        }
       return true;
   }



}
