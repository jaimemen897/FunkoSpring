package com.example.springfunko.rest.funkos.exception;

public class FunkoNotStock extends FunkoException{
    public FunkoNotStock(String id) {
        super("Funko " + id + " has no stock");
    }
}
