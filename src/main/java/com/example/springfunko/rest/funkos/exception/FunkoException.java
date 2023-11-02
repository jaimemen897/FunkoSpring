package com.example.springfunko.rest.funkos.exception;

public abstract class FunkoException extends RuntimeException {

    public FunkoException(String message) {
        super(message);
    }
}
