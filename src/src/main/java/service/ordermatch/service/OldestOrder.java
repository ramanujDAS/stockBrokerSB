package service.ordermatch.service;

import service.model.Order;
import service.ordermatch.OrderTimestampComparator;

import java.util.List;
import java.util.Optional;

public class OldestOrder implements IMatchingStrategy {
    @Override
    public Optional<Order> match(List<Order> orders) {
        if (orders.isEmpty()) {
            return Optional.empty();
        }
        orders.sort(new OrderTimestampComparator());
        return Optional.of(orders.get(0));
    }
}
