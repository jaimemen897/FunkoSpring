package com.example.springfunko.rest.orders.service;

import com.example.springfunko.rest.orders.models.Order;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Page<Order> findAll(Pageable pageable);

    Order findById(ObjectId id);

    Page<Order> findByUserId(Long idUser, Pageable pageable);

    Order save(Order order);

    void delete(ObjectId objectId);

    Order update(ObjectId objectId, Order order);
}
