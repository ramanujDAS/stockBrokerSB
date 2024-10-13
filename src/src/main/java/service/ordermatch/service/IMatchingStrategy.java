package service.ordermatch.service;

import service.model.Order;

import java.util.List;
import java.util.Optional;

public interface IMatchingStrategy {

    public Optional<Order> match(List<Order> orders);
}
