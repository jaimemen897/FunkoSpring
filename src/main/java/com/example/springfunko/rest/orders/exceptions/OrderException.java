package com.example.springfunko.rest.orders.exceptions;

public abstract class OrderException extends RuntimeException {
    protected OrderException(String message) {
        super(message);
    }
}
