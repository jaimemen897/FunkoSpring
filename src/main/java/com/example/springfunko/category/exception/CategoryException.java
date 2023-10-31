package com.example.springfunko.category.exception;

public abstract class CategoryException extends RuntimeException {

    protected CategoryException(String message) {
        super(message);
    }
}
