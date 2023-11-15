package com.example.springfunko.rest.orders.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class OrderNotFound extends OrderException {
    public OrderNotFound(String message) {
        super(message);
    }
}
