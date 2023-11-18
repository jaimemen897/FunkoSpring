package com.example.springfunko.rest.orders.service;

import com.example.springfunko.rest.funkos.exception.FunkoBadPrice;
import com.example.springfunko.rest.funkos.exception.FunkoNotFound;
import com.example.springfunko.rest.funkos.exception.FunkoNotStock;
import com.example.springfunko.rest.funkos.repositories.FunkoRepository;
import com.example.springfunko.rest.orders.exceptions.OrderNotFound;
import com.example.springfunko.rest.orders.exceptions.OrderNotItems;
import com.example.springfunko.rest.orders.models.Order;
import com.example.springfunko.rest.orders.models.OrderLine;
import com.example.springfunko.rest.orders.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@CacheConfig(cacheNames = {"orders"})
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final FunkoRepository funkoRepository;

    public OrderServiceImpl(OrderRepository orderRepository, FunkoRepository funkoRepository) {
        this.orderRepository = orderRepository;
        this.funkoRepository = funkoRepository;
    }

    @Override
    public Page<Order> findAll(Pageable pageable) {
        log.info("Find all orders");
        return orderRepository.findAll(pageable);
    }

    @Override
    @Cacheable(key = "#id")
    public Order findById(ObjectId id) {
        log.info("Find order by id: {}", id);
        return orderRepository.findById(id).orElseThrow(() -> new OrderNotFound(id.toHexString()));
    }

    @Override
    public Page<Order> findByUserId(Long idUser, Pageable pageable) {
        log.info("Find order by customer id: {}", idUser);
        return orderRepository.findByIdUser(idUser, pageable);
    }

    @Override
    @Transactional
    @CachePut(key = "#result.id")
    public Order save(Order order) {
        log.info("Save order: {}", order);
        checkOrder(order);
        var orderToSave = reserveStockOrder(order);

        orderToSave.setCreatedAt(LocalDateTime.now());
        orderToSave.setUpdatedAt(LocalDateTime.now());

        return orderRepository.save(orderToSave);
    }

    @Override
    @Transactional
    @CacheEvict
    public void delete(ObjectId objectId) {
        log.info("Delete order by id: {}", objectId);
        var orderToDelete = orderRepository.findById(objectId)
                .orElseThrow(() -> new OrderNotFound(objectId.toHexString()));
        returnStockOrders(orderToDelete);
        orderRepository.delete(orderToDelete);
    }

    @Override
    @Transactional
    @CachePut(key = "#objectId")
    public Order update(ObjectId objectId, Order order) {
        log.info("Update order by id: {}", objectId);
        Order orderToUpdate = orderRepository.findById(objectId).orElseThrow(() -> new OrderNotFound(objectId.toHexString()));

        returnStockOrders(orderToUpdate);
        checkOrder(order);
        var orderToSave = reserveStockOrder(order);
        orderToSave.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(orderToSave);
    }

    Order reserveStockOrder(Order order) {
        log.info("Reserve stock for order: {}", order);

        if (order.getOrderLines() == null || order.getOrderLines().isEmpty()) {
            throw new OrderNotItems(order.getId().toHexString());
        }

        order.getOrderLines().forEach(orderLine -> {
            var funko = funkoRepository.findById(orderLine.getIdFunko()).get();
            funko.setCantidad(funko.getCantidad() - orderLine.getQuantity());
            funkoRepository.save(funko);
            orderLine.setTotal(orderLine.getQuantity() * funko.getPrecio());
        });

        var total = order.getOrderLines().stream()
                .map(orderLine -> orderLine.getQuantity() * orderLine.getPrice())
                .reduce(0.0, Double::sum);

        var totalItems = order.getOrderLines().stream()
                .map(OrderLine::getQuantity)
                .reduce(0, Integer::sum);

        order.setTotal(total);
        order.setTotalItems(totalItems);

        return order;
    }

    void checkOrder(Order order) {
        log.info("Check order: {}", order);

        if (order.getOrderLines() == null || order.getOrderLines().isEmpty()) {
            throw new OrderNotItems(order.getId().toHexString());
        }
        order.getOrderLines().forEach(orderLine -> {
            var funko = funkoRepository.findById(orderLine.getIdFunko())
                    .orElseThrow(() -> new FunkoNotFound("Funko not found with id: " + orderLine.getIdFunko()));
            if (funko.getCantidad() < orderLine.getQuantity() && orderLine.getQuantity() > 0) {
                throw new FunkoNotStock("Funko stock is: " + funko.getCantidad() + " and order quantity is: " + orderLine.getQuantity());
            }
            if (!funko.getPrecio().equals(orderLine.getPrice())) {
                throw new FunkoBadPrice("Funko price is: " + funko.getPrecio() + " and order price is: " + orderLine.getPrice());
            }
        });
    }

    Order returnStockOrders(Order order){
        log.info("Return stock for order: {}", order);
        if (order.getOrderLines() != null){
            order.getOrderLines().forEach(orderLine -> {
                var funko = funkoRepository.findById(orderLine.getIdFunko()).get();
                funko.setCantidad(funko.getCantidad() + orderLine.getQuantity());
                funkoRepository.save(funko);
            });
        }
        return order;
    }
}
