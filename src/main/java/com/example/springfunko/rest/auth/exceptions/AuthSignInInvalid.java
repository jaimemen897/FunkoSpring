package com.example.springfunko.rest.auth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AuthSignInInvalid extends AuthException{
    public AuthSignInInvalid(String message) {
        super(message);
    }
}
