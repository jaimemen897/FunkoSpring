package com.example.springfunko.rest.funkos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FunkoBadPrice extends FunkoException{
    public FunkoBadPrice(String message) {
        super(message);
    }
}
