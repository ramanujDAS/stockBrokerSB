package repository.model;

import model.OrderStatus;
import model.OrderType;
import model.StockSymbol;

import java.util.Objects;

public class OrderDao {
    private long orderId;
    private long userId;
    private OrderType orderType;
    private StockSymbol stockSymbol;
    private int quantity;
    private double price;
    private OrderStatus status;
    private long timeStamp;
    private Long orderExpiryInMinutes;

    public OrderDao(long orderId, long userId, OrderType orderType, StockSymbol stockSymbol, int quantity, double price, OrderStatus status, long timeStamp) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderType = orderType;
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
        this.timeStamp = timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderDao )) return false;
        OrderDao orders = (OrderDao) o;
        return this.orderId == orders.orderId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(orderId);
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public StockSymbol getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(StockSymbol stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "OrderDao{" +
                "orderId=" + orderId +
                ", userId=" + userId +
                ", orderType=" + orderType +
                ", stockSymbol=" + stockSymbol +
                ", quantity=" + quantity +
                ", price=" + price +
                ", status=" + status +
                ", timeStamp=" + timeStamp +
                '}' +"\n";
    }

    public Long getOrderExpiryInMinutes() {
        return orderExpiryInMinutes;
    }

    public void setOrderExpiryInMinutes(Long orderExpiryInMinutes) {
        this.orderExpiryInMinutes = orderExpiryInMinutes;
    }
}
