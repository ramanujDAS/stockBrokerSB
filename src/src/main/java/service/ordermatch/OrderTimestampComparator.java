package service.ordermatch;

import service.model.Order;

import java.util.Comparator;

public class OrderTimestampComparator implements Comparator<Order> {

    @Override
    public int compare(Order o1, Order o2) {
        return Long.compare(o1.getTimeStamp(), o2.getTimeStamp());
    }
}

