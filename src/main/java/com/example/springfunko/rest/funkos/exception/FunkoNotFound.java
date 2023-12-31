package com.example.springfunko.rest.funkos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FunkoNotFound extends FunkoException {

    public FunkoNotFound(String message) {
        super(message);
    }
}
