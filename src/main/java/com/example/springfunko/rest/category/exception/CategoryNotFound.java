package com.example.springfunko.rest.category.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CategoryNotFound extends CategoryException {

    public CategoryNotFound(String message) {
        super(message);
    }
}
