package com.example.springfunko.rest.category.exception;

public abstract class CategoryException extends RuntimeException {

    protected CategoryException(String message) {
        super(message);
    }
}
