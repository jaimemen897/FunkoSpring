package com.example.springfunko.funkos.exception;

public abstract class FunkoException extends RuntimeException {

    public FunkoException(String message) {
        super(message);
    }
}
