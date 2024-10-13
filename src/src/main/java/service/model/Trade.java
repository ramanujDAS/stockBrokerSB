package service.model;

import model.StockSymbol;
import repository.TradeType;

import java.util.Objects;

public class Trade {

    private long tradeId;
    private TradeType tradeType;
    private long buyerOrderId;
    private long sellerOrderId;
    private StockSymbol stockSymbol;
    private int quantity;
    private double price;
    private long timeStamp;

    public Trade(long tradeId, TradeType tradeType, long buyerOrderId, long sellerOrderId, StockSymbol stockSymbol, int quantity, double price, long timeStamp) {
        this.tradeId = tradeId;
        this.tradeType = tradeType;
        this.buyerOrderId = buyerOrderId;
        this.sellerOrderId = sellerOrderId;
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.price = price;
        this.timeStamp = timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trade )) return false;
        Trade trades = (Trade) o;
        return tradeId == trades.tradeId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tradeId);
    }

    public long getTradeId() {
        return tradeId;
    }

    public void setTradeId(long tradeId) {
        this.tradeId = tradeId;
    }

    public TradeType getTradeType() {
        return tradeType;
    }

    public void setTradeType(TradeType tradeType) {
        this.tradeType = tradeType;
    }

    public long getBuyerOrderId() {
        return buyerOrderId;
    }

    public void setBuyerOrderId(long buyerOrderId) {
        this.buyerOrderId = buyerOrderId;
    }

    public long getSellerOrderId() {
        return sellerOrderId;
    }

    public void setSellerOrderId(long sellerOrderId) {
        this.sellerOrderId = sellerOrderId;
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

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
