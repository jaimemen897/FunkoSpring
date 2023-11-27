package com.example.springfunko.rest.users.exceptions;

public class UnauthorizedUser extends UserException{
    public UnauthorizedUser(String message) {
        super(message);
    }
}
