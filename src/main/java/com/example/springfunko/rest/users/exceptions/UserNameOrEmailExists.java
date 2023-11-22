package com.example.springfunko.rest.users.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNameOrEmailExists extends UserException {
    public UserNameOrEmailExists(String message) {
        super(message);
    }
}